/**
 * oat
 * 
 * An application with that you can make raw http requests to any url. You can 
 * save a request for later use. The application uses the java nio library to 
 * make non-blocking requests so the requests should work fluently.
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of tajet. tajet is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * tajet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with tajet. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.oat.http;

import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.k42b3.oat.icallback;
import com.k42b3.oat.iresponse_filter;

/**
 * http
 *
 * This is the main class wich handles all http requests. The class uses the NIO
 * library to make non-blocking requests. We write the http request to the
 * socket and start reading the response. We first try to find the end of the
 * header response so we search for the byte sequence "0xD 0xA 0xD 0xA" wich 
 * means \r\n\r\n. We split the byte buffer into the header (everything before) 
 * and body (everything after). We encode the header in UTF-8 and look for the 
 * content-lenght header field. If no content length header is set we look 
 * whether transfer-encoding is equal to chunked else we throw an exception.
 *
 * This works all fine but there are a few problems with this implementation in
 * some cases. The problem is that the byte sequence "0xD 0xA 0xD 0xA" can be
 * always between two buffers so we dont find the header. In example:
 * 
 * first buffer
 * +--------------
 * | 1    = [data]
 * | .    = [data]
 * | 1023 = 0xD = \r
 * | 1024 = 0xA = \n
 * +----------
 * 
 * second buffer
 * +--------
 * | 1    = 0xD = \r
 * | 2    = 0xA = \n
 * | .    = [data]
 * | 1024 = [data]
 * +--------
 *
 * In this case we dont find the byte sequence "0xD 0xA 0xD 0xA" not in the 
 * first buffer nor in the second. In most cases the header should not be 
 * between two buffers but if you have problems change the buffer size to 
 * another value. 
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class http implements Runnable
{
	public final static String new_line = "\r\n";
	public final static String type = "HTTP/1.1";
	public final static String method = "GET";
	public final static String url = "/";

	private static Selector selector;
	private SocketChannel channel = null;

	/**
	 * The buffer size. Increase the size if you expect header greater then 1024
	 * bytes to avoid the problesm described in the comment
	 */
	private int buffer_size = 1024;
	
	/**
	 * The response header as string
	 */
	private String header = "";

	/**
	 * Holds the complete body in binary format the byte buffer is build from
	 * the buffer_list
	 */
	private ByteBuffer body;

	/**
	 * The found content length of the body
	 */
	private int content_length = -1;

	/**
	 * Indicates whether we have found the header
	 */
	private boolean found_header = false;

	/**
	 * Indicates whether the transfer encoding is chunked
	 */
	private boolean chunked = false;

	private ArrayList<ByteBuffer> buffer_header_list = new ArrayList<ByteBuffer>();
	private ArrayList<ByteBuffer> buffer_body_list = new ArrayList<ByteBuffer>();
	
	private int read = 0;
	
	private CharsetDecoder default_decoder;
	private CharsetEncoder default_encoder;

	private String host;
	private int port;
	private request request;
	private response response;
	private icallback callback;

	private ArrayList<iresponse_filter> response_filter = new ArrayList<iresponse_filter>();

	public http(String raw_url, request request, icallback callback) throws Exception
	{
		// get host and port from url
		URL url;
		
		if(raw_url.startsWith("http://") || raw_url.startsWith("https://"))
		{
			url = new URL(raw_url);
		}
		else
		{
			url = new URL("http://" + raw_url);
		}
	
		this.host = url.getHost();
		this.port = url.getPort() == -1 ? 80 : url.getPort();
		
		
		// set params
		this.request = request;
		this.callback = callback;
	}

	public void add_response_filter(iresponse_filter filter)
	{
		this.response_filter.add(filter);
	}

	public void run()
	{
		try
		{
			SocketChannel channel = null;


			InetSocketAddress socket_address = new InetSocketAddress(this.host, this.port);


			// charset
			Charset default_charset = Charset.forName("UTF-8");

			this.default_decoder = default_charset.newDecoder();
			this.default_encoder = default_charset.newEncoder();


			// allocate buffer
			ByteBuffer buffer_temp;
			ByteBuffer buffer = ByteBuffer.allocateDirect(this.buffer_size);


			// connect
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(socket_address);

			selector = Selector.open();

			channel.register(selector, SelectionKey.OP_CONNECT);

			while(selector.select(500) > 0) 
			{
				Set ready_keys = selector.selectedKeys();
				
				Iterator ready_itor = ready_keys.iterator();

				while(ready_itor.hasNext())
				{
					SelectionKey key = (SelectionKey) ready_itor.next();
					
					ready_itor.remove();

					SocketChannel key_channel = (SocketChannel) key.channel();

					if(key.isConnectable())
					{
			            if(key_channel.isConnectionPending()) 
			            {
			            	key_channel.finishConnect();
			            }

			            channel.register(selector, SelectionKey.OP_WRITE);
					}
					else if(key.isReadable())
					{
						key_channel.read(buffer);

						buffer.flip();


						if(!this.found_header)
						{
							// search the byte buffer for \r\n\r\n add the
							// bytes before to the header buffer the rest
							// to the body buffer
							for(int i = 0; i < buffer.remaining(); i++)
							{
								// look whether we are at \r\n\r\n
								if(i > 3 &&
								buffer.get(i - 3) == 0xD && 
								buffer.get(i - 2) == 0xA && 
								buffer.get(i - 1) == 0xD && 
								buffer.get(i)     == 0xA)
								{
									this.found_header = true;

									buffer.position(i + 1);

									break;
								}
							}


							// if we dont find the header in the first buffer
							// we throw an exception because we are not able to
							// parse the response
							if(!this.found_header)
							{
								this.read+= buffer.limit();
								
								buffer_temp = ByteBuffer.allocateDirect(buffer.limit());

								buffer_temp.put(buffer);

								this.buffer_header_list.add(buffer_temp);
							}
							else
							{
								this.read+= buffer.position();

								buffer_temp = ByteBuffer.allocateDirect(buffer.position());

								for(int i = 0; i < buffer.position(); i++)
								{
									buffer_temp.put(buffer.get(i));
								}

								this.buffer_header_list.add(buffer_temp);


								// get complete header
								ByteBuffer buffer_header = this.merge_buffer(this.buffer_header_list, this.read);

								
								// decode header as UTF-8
								CharBuffer char_buffer_header = CharBuffer.allocate(this.read);
								
								this.default_decoder.decode(buffer_header, char_buffer_header, false);

								char_buffer_header.flip();


								// reset read count
								this.read = 0;
								
								
								// parse header
								this.header = char_buffer_header.toString();


								// search for content-length or transfer-encoding
								Map<String, String> header = util.parse_header(this.header, http.new_line);

								if(header.containsKey("Content-Length"))
								{
									this.content_length = Integer.parseInt(header.get("Content-Length"));
								}
								else if(header.containsKey("Transfer-Encoding") && header.get("Transfer-Encoding").equals("chunked"))
								{
									this.chunked = true;
								}
								else
								{
									throw new Exception("Couldnt find Content-Length or Transfer-Encoding header in response");
								}


								// clear header buffer
								char_buffer_header.clear();


								// add read
								this.read+= buffer.limit() - buffer.position();


								// add buffer to list
								buffer_temp = ByteBuffer.allocateDirect(buffer.slice().limit());

								buffer_temp.put(buffer);
								
								this.buffer_body_list.add(buffer_temp);
							}
						}
						else
						{
							// add read
							this.read+= buffer.limit();


							// add buffer to list
							buffer_temp = ByteBuffer.allocateDirect(buffer.limit());

							buffer_temp.put(buffer);

							this.buffer_body_list.add(buffer_temp);
							
							
							// close channel if ready
							if(this.chunked)
							{
								// check for 0\r\n\r\n
								int i = buffer.limit();

								if(i > 4 &&
								buffer.get(i - 4) == 0x30 && 
								buffer.get(i - 3) == 0xD  && 
								buffer.get(i - 2) == 0xA  && 
								buffer.get(i - 1) == 0xD  &&
								buffer.get(i)     == 0xA)
								{
									channel.close();
								}
							}
							else
							{
								// check content length
								if(this.read >= this.content_length)
								{
									channel.close();
								}
							}
						}


						// clear buffer
						buffer.clear();
					}
					else if(key.isWritable())
					{
						key_channel.write(default_encoder.encode(CharBuffer.wrap(request.get_http_message())));

						channel.register(selector, SelectionKey.OP_READ);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(channel != null) 
			{
				try
				{
					channel.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}


			// build body ByteBuffer from buffer_list
			this.body = this.merge_buffer(this.buffer_body_list, this.read);


			// create response
			this.response = new response(this.header, this.body);


			// apply response filter 
			for(int i = 0; i < this.response_filter.size(); i++)
			{
				this.response_filter.get(i).exec(this.response);
			}


			callback.response(this.response.toString());
		}
	}

	public request get_request()
	{
		return this.request;
	}
	
	public response get_response()
	{
		return this.response;
	}
	
	private ByteBuffer merge_buffer(ArrayList<ByteBuffer> list, int capacity)
	{
		ByteBuffer buffer = ByteBuffer.allocateDirect(capacity);

		for(int i = 0; i < list.size(); i++)
		{
			ByteBuffer buf = list.get(i);

			buf.rewind();

			for(int j = 0; j < buf.remaining(); j++)
			{
				if(buffer.hasRemaining())
				{
					buffer.put(buf.get(j));
				}
			}
		}

		buffer.flip();
		
		return buffer;
	}
}
