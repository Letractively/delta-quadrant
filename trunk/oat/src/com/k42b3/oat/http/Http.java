/**
 * oat
 * 
 * An application with that you can make raw http requests to any url. You can 
 * save a request for later use. The application uses the java nio library to 
 * make non-blocking requests so the requests should work fluently.
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of oat. oat is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * oat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with oat. If not, see <http://www.gnu.org/licenses/>.
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
import java.util.logging.Logger;

import com.k42b3.oat.CallbackInterface;
import com.k42b3.oat.Oat;
import com.k42b3.oat.ResponseFilterInterface;

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
public class Http implements Runnable
{
	public final static String newLine = "\r\n";
	public final static String type = "HTTP/1.1";
	public final static String method = "GET";
	public final static String url = "/";

	private static Selector selector;
	private SocketChannel channel = null;

	/**
	 * The buffer size. Increase the size if you expect header greater then 1024
	 * bytes to avoid the problesm described in the comment
	 */
	private int bufferSize = 1024;

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
	private int contentLength = -1;

	/**
	 * Indicates whether we have found the header
	 */
	private boolean foundHeader = false;

	/**
	 * Indicates whether the transfer encoding is chunked
	 */
	private boolean chunked = false;

	private ArrayList<ByteBuffer> bufferHeaderList = new ArrayList<ByteBuffer>();
	private ArrayList<ByteBuffer> bufferBodyList = new ArrayList<ByteBuffer>();

	private int read = 0;

	private CharsetDecoder defaultDecoder;
	private CharsetEncoder defaultEncoder;

	private String host;
	private int port;
	private Request request;
	private Response response;
	private CallbackInterface callback;

	private ArrayList<ResponseFilterInterface> responseFilter = new ArrayList<ResponseFilterInterface>();

	private Logger logger = Logger.getLogger("com.k42b3.oat");

	public Http(String rawUrl, Request request, CallbackInterface callback) throws Exception
	{
		// get host and port from url
		URL url;

		if(rawUrl.startsWith("http://") || rawUrl.startsWith("https://"))
		{
			url = new URL(rawUrl);
		}
		else
		{
			url = new URL("http://" + rawUrl);
		}

		this.host = url.getHost();
		this.port = url.getPort() == -1 ? 80 : url.getPort();


		// set params
		this.request = request;
		this.callback = callback;
	}

	public void addResponseFilter(ResponseFilterInterface filter)
	{
		this.responseFilter.add(filter);
	}

	public void run()
	{
		try
		{
			SocketChannel channel = null;


			InetSocketAddress socketAddress = new InetSocketAddress(this.host, this.port);


			// charset
			Charset defaultCharset = Charset.forName("UTF-8");

			this.defaultDecoder = defaultCharset.newDecoder();
			this.defaultEncoder = defaultCharset.newEncoder();


			// allocate buffer
			ByteBuffer bufferTemp;
			ByteBuffer buffer = ByteBuffer.allocateDirect(this.bufferSize);


			// connect
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(socketAddress);

			logger.info("Open socket for address " + socketAddress.getAddress().getHostAddress());

			selector = Selector.open();

			channel.register(selector, SelectionKey.OP_CONNECT);

			while(selector.select(500) > 0) 
			{
				Set readyKeys = selector.selectedKeys();
				Iterator readyIterator = readyKeys.iterator();

				while(readyIterator.hasNext())
				{
					SelectionKey key = (SelectionKey) readyIterator.next();

					readyIterator.remove();

					SocketChannel keyChannel = (SocketChannel) key.channel();

					if(key.isConnectable())
					{
			            if(keyChannel.isConnectionPending()) 
			            {
			            	keyChannel.finishConnect();
			            }

			            channel.register(selector, SelectionKey.OP_WRITE);
					}
					else if(key.isReadable())
					{
						keyChannel.read(buffer);

						buffer.flip();


						if(!this.foundHeader)
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
									this.foundHeader = true;

									buffer.position(i + 1);

									break;
								}
							}


							// if we dont find the header in the first buffer
							// add the buffer to the list
							if(!this.foundHeader)
							{
								this.read+= buffer.limit();

								bufferTemp = ByteBuffer.allocateDirect(buffer.limit());

								bufferTemp.put(buffer);

								this.bufferHeaderList.add(bufferTemp);
							}
							else
							{
								this.read+= buffer.position();

								bufferTemp = ByteBuffer.allocateDirect(buffer.position());

								for(int i = 0; i < buffer.position(); i++)
								{
									bufferTemp.put(buffer.get(i));
								}

								this.bufferHeaderList.add(bufferTemp);


								// get complete header
								ByteBuffer bufferHeader = this.mergeBuffer(this.bufferHeaderList, this.read);


								// decode header as UTF-8
								CharBuffer charBufferHeader = CharBuffer.allocate(this.read);

								this.defaultDecoder.decode(bufferHeader, charBufferHeader, false);

								charBufferHeader.flip();


								logger.info("Found header end after " + this.read + " bytes");


								// reset read count
								this.read = 0;
								
								
								// parse header
								this.header = charBufferHeader.toString();


								// search for content-length or transfer-encoding
								Map<String, String> header = Util.parseHeader(this.header, Http.newLine);

								if(header.containsKey("Content-Length"))
								{
									this.contentLength = Integer.parseInt(header.get("Content-Length"));

									logger.info("Found content length " + this.contentLength);
								}
								else if(header.containsKey("Transfer-Encoding") && header.get("Transfer-Encoding").equals("chunked"))
								{
									this.chunked = true;

									logger.info("Set transfer encoding to chunked");
								}
								else
								{
									throw new Exception("Couldnt find Content-Length or Transfer-Encoding header in response");
								}


								// clear header buffer
								charBufferHeader.clear();


								// add read
								this.read+= buffer.limit() - buffer.position();


								// add buffer to list
								bufferTemp = ByteBuffer.allocateDirect(buffer.slice().limit());

								bufferTemp.put(buffer);

								this.bufferBodyList.add(bufferTemp);
							}
						}
						else
						{
							// add read
							this.read+= buffer.limit();


							// add buffer to list
							bufferTemp = ByteBuffer.allocateDirect(buffer.limit());

							bufferTemp.put(buffer);

							this.bufferBodyList.add(bufferTemp);


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
								if(this.read >= this.contentLength)
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
						CharBuffer buf = CharBuffer.wrap(request.getHttpMessage());

						logger.info("Write " + buf.length() + " bytes to socket");

						keyChannel.write(defaultEncoder.encode(buf));

						channel.register(selector, SelectionKey.OP_READ);
					}
				}
			}
		}
		catch(Exception e)
		{
			Oat.handleException(e);
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
					Oat.handleException(e);
				}
			}


			logger.info("Read complete " + this.read + " bytes");


			// build body ByteBuffer from buffer_list
			this.body = this.mergeBuffer(this.bufferBodyList, this.read);


			// create response
			this.response = new Response(this.header, this.body);


			// apply response filter 
			for(int i = 0; i < this.responseFilter.size(); i++)
			{
				try
				{
					this.responseFilter.get(i).exec(this.response);
				}
				catch(Exception e)
				{
					Oat.handleException(e);
				}
			}


			callback.response(this.response.toString());
		}
	}

	public Request getRequest()
	{
		return this.request;
	}

	public Response getResponse()
	{
		return this.response;
	}

	private ByteBuffer mergeBuffer(ArrayList<ByteBuffer> list, int capacity)
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
