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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.k42b3.oat.icallback;
import com.k42b3.oat.iresponse_filter;

/**
 * http
 *
 * This is the main class wich handles all http requests. The class uses the NIO
 * library to make non-blocking requests. We write the http request to the
 * socket and start reading the response. We first read 1024 bytes and search
 * for the byte sequence "0xD 0xA 0xD 0xA" wich means \n\r\n\r. We split the
 * byte buffer into the header (everything before) and body (everything after).
 * We encode the header in UTF-8 and look for the content-lenght and 
 * content-type header fields. If the body length is greater or equal content 
 * lenght we close the chanel. If no content length header is set we close the 
 * channel if there are no more bytes to read.
 *
 * We first read the header because we want parse the body maybe in another 
 * charset so we read the header always in UTF-8 and then we look at the 
 * content-type and if there is another charset specified we use this else we 
 * use UTF-8
 *
 * This works all fine but there are a few problems with this implementation in
 * some cases. If the header is greater then the buffer means 1024 bytes then
 * we are not able to get the header files so we throw an "Max header size 
 * exceeded" exception. Probably you ask yourself why not reading the header
 * in the next round the problem is that the byte sequence "0xD 0xA 0xD 0xA" can
 * always be between two buffers so we dont find the header. In example:
 * 
 * first buffer
 * +--------------
 * | 1    = [data]
 * | .    = [data]
 * | 1023 = 0xD = \n
 * | 1024 = 0xA = \r
 * +----------
 * 
 * second buffer
 * +--------
 * | 1    = 0xD = \n
 * | 2    = 0xA = \r
 * | .    = [data]
 * | 1024 = [data]
 * +--------
 *
 * In this case we dont find the byte sequence "0xD 0xA 0xD 0xA" not in the 
 * first buffer nor in the second. In most cases the header should not exceed
 * 1024 bytes but if you expect larger headers you can increase the byte buffer
 * size. Sure we could solve this problem by looking at the end of the buffer 
 * and at the beginning of the next buffer but because this case doesnt often
 * occure it the expenditure is greater then the benefit ... if you have a 
 * solution do not hesiatte to contact me
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
	 * bytes to avoid the problesm described in the header
	 */
	private int buffer_size = 1024;
	
	/**
	 * The response header as string
	 */
	private String header;

	/**
	 * The response body as string
	 */
	private StringBuilder body = new StringBuilder();

	/**
	 * Holds the complete body in raw binary format
	 */
	private ByteBuffer raw_body;

	/**
	 * The found content length of the body
	 */
	private int content_length = -1;

	/**
	 * Indicates whether we have found the header
	 */
	private boolean found_header = false;
	
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
			this.body = new StringBuilder();

			SocketChannel channel = null;


			InetSocketAddress socket_address = new InetSocketAddress(this.host, this.port);


			// charset
			Charset default_charset = Charset.forName("UTF-8");

			CharsetDecoder default_decoder = default_charset.newDecoder();
			CharsetEncoder default_encoder = default_charset.newEncoder();

			CharsetDecoder body_decoder = default_charset.newDecoder();

			
			// allocate buffer
			ByteBuffer buffer_header = ByteBuffer.allocateDirect(this.buffer_size);
			ByteBuffer buffer = ByteBuffer.allocateDirect(this.buffer_size);
			CharBuffer char_buffer_header = CharBuffer.allocate(this.buffer_size);
			CharBuffer char_buffer = CharBuffer.allocate(this.buffer_size);


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


						// parse the first response for the header
						if(!this.found_header)
						{
							// search the byte buffer for \n\r\n\r add the
							// bytes before to the header buffer the rest
							// to the body buffer
							int header_end = 0;

							for(int i = 0; i < this.buffer_size; i++)
							{
								buffer_header.put(i, buffer.get(i));

								// look whether we are at \r\n\r\n
								if(buffer.get(i)  == 0xD && 
								buffer.get(i + 1) == 0xA && 
								buffer.get(i + 2) == 0xD && 
								buffer.get(i + 3) == 0xA)
								{
									this.found_header = true;

									header_end = i + 4;

									break;
								}
							}


							// if we dont find the header in the first buffer
							// we throw an exception because we are not able to
							// parse the response
							if(!this.found_header || header_end > this.buffer_size)
							{
								throw new Exception("Max header size exceeded");
							}
							
							
							// decode header as UTF-8
							default_decoder.decode(buffer_header, char_buffer_header, false);

							char_buffer_header.flip();


							// parse header
							this.header = char_buffer_header.toString();


							// search for content-length
							HashMap<String, String> header = util.parse_header(this.header, http.new_line);

							if(header.containsKey("Content-Length"))
							{
								this.content_length = Integer.parseInt(header.get("Content-Length"));
							}

							
							// check character set
							if(header.containsKey("Content-Type"))
							{
								body_decoder = util.get_content_type_charset(header.get("Content-Type")).newDecoder();
							}


							// clear header buffer
							char_buffer_header.clear();


							// add rest to body
							ByteBuffer buffer_body = ByteBuffer.allocateDirect(this.buffer_size - header_end);

							int j = 0;

							for(int i = header_end; i < this.buffer_size; i++)
							{
								buffer_body.put(j, buffer.get(i));
								
								j++;
							}

							buffer = buffer_body;
						}


						// add to byte body
						if(this.raw_body == null)
						{
							this.raw_body = ByteBuffer.allocateDirect(this.content_length);
						}

						this.raw_body.put(buffer.duplicate());


						// decode response
						body_decoder.decode(buffer, char_buffer, false);


						// append text
						char_buffer.flip();

						this.body.append(char_buffer);


						// check content length
						if(this.content_length != -1)
						{
							if(this.body.length() >= this.content_length)
							{
								//key.cancel();
								channel.close();
							}
						}
						else
						{
							if(char_buffer.length() == 0)
							{
								//key.cancel();
								channel.close();
							}
						}


						// clear buffer
						buffer.clear();
						
						char_buffer.clear();
					}
					else if(key.isWritable())
					{
						key_channel.write(default_encoder.encode(CharBuffer.wrap(request.toString())));

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


			// create response
			this.response = new response(this.header + http.new_line + http.new_line + this.body.toString());

			this.response.set_raw_body(this.raw_body);


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
}
