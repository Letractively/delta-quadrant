/**
 * oat
 * 
 * An application to send raw http requests to any host. It is designed to
 * debug and test web applications. You can apply filters to the request and
 * response wich can modify the content.
 * 
 * Copyright (c) 2010, 2011 Christoph Kappestein <k42b3.x@gmail.com>
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

import com.k42b3.oat.Oat;
import com.k42b3.oat.filter.CallbackInterface;
import com.k42b3.oat.filter.ResponseFilterAbstract;

/**
 * Http
 *
 * This is the main class wich handles all http requests. The class uses the NIO
 * library to make non-blocking requests. We write the http request to the
 * socket and start reading the response. We first try to find the end of the
 * header response so we search for the byte sequence "0xD 0xA 0xD 0xA" wich 
 * means \r\n\r\n. We encode the header in UTF-8 and look for the content-lenght 
 * header field. If no content length header is set we look whether 
 * transfer-encoding is equal to chunked else we throw an exception.
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
	 * The buffer size.
	 */
	private int bufferSize = 1024;

	/**
	 * The buffer size to read the next chunks size if transfer encoding is
	 * chunked
	 */
	private int bufferSizeSmall = 64;

	/**
	 * Contains the UTF-8 decoded header
	 */
	private String header;

	/**
	 * The found content length of the body
	 */
	private int contentLength = 0;

	/**
	 * If transfer encoding is chunked this contains the size of the current
	 * chunk
	 */
	private int chunkLength = 0;

	/**
	 * Indicates whether we have found the header
	 */
	private boolean foundHeader = false;
	private boolean readChunkLength = false;
	private boolean chunkEnd = false;

	/**
	 * Indicates whether the transfer encoding is chunked
	 */
	private boolean isContentLength = false;
	private boolean isChunked = false;

	/**
	 * How often we can receive an 0 bytes buffer before we close the connection
	 */
	private int maxZeroRounds = 8;

	/**
	 * Default encoder for reading the header
	 */
	private CharsetDecoder defaultDecoder;
	private CharsetEncoder defaultEncoder;

	private String host;
	private int port;
	private Request request;
	private Response response;
	private CallbackInterface callback;

	private ArrayList<ResponseFilterAbstract> responseFilter = new ArrayList<ResponseFilterAbstract>();
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


		// charset
		Charset defaultCharset = Charset.forName("UTF-8");

		this.defaultDecoder = defaultCharset.newDecoder();
		this.defaultEncoder = defaultCharset.newEncoder();
	}

	public void addResponseFilter(ResponseFilterAbstract filter)
	{
		this.responseFilter.add(filter);
	}

	public void run()
	{
		try
		{
			SocketChannel channel = null;
			InetSocketAddress socketAddress = new InetSocketAddress(this.host, this.port);


			this.isContentLength = false;
			this.isChunked = false;


			// buffers
			ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
			Buffer bufferHeader = new Buffer(bufferSize);
			Buffer bufferBody = new Buffer(bufferSize);
			Buffer bufferChunked = new Buffer(bufferSize);


			// counts zero rounds
			int zeroRounds = 0;


			// connect
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(socketAddress);

			logger.info("Open socket for address " + socketAddress.getAddress().getHostAddress());

			selector = Selector.open();

			channel.register(selector, SelectionKey.OP_CONNECT);

			while(selector.select(500) > 0) 
			{
				Set<SelectionKey> readyKeys = selector.selectedKeys();
				Iterator<SelectionKey> readyIterator = readyKeys.iterator();

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
						if(this.readChunkLength)
						{
							buffer = ByteBuffer.allocate(this.bufferSizeSmall);
						}
						// if we have a content length allocate the size to
						// the buffer
						else if(this.contentLength > 0)
						{
							buffer = ByteBuffer.allocate(this.contentLength);
						}
						// if we have a chunk length allocate this size to the
						// buffer
						else if(this.chunkLength > 0)
						{
							buffer = ByteBuffer.allocate(this.chunkLength);
						}


						keyChannel.read(buffer);

						buffer.flip();


						logger.info("Received " + buffer.remaining() + " bytes");


						if(!this.foundHeader)
						{
							// search the byte buffer for \r\n\r\n add the
							// bytes before to the header buffer the rest
							// to the body buffer
							for(int i = 0; i < buffer.remaining(); i++)
							{
								bufferHeader.add(buffer.get(i));

								if(buffer.get(i) == 0xA && bufferHeader.containsRNRN())
								{
									this.foundHeader = true;

									buffer.position(i + 1);

									break;
								}
							}

							if(this.foundHeader)
							{
								logger.info("Read header " + bufferHeader.getSize() + " bytes");


								// decode header as UTF-8
								CharBuffer charBufferHeader = CharBuffer.allocate(bufferHeader.getSize());

								this.defaultDecoder.decode(bufferHeader.getByteBuffer(), charBufferHeader, false);

								charBufferHeader.flip();


								// search for Content-Length or Transfer-Encoding
								this.header = charBufferHeader.toString();

								Map<String, String> header = Util.parseHeader(this.header, Http.newLine);

								if(header.containsKey("Content-Length"))
								{
									this.isContentLength = true;
									this.contentLength = Integer.parseInt(header.get("Content-Length"));

									logger.info("Found content length " + this.contentLength);
								}
								else if(header.containsKey("Transfer-Encoding") && header.get("Transfer-Encoding").equals("chunked"))
								{
									this.isChunked = true;
									this.chunkLength = 0;

									logger.info("Set transfer encoding to chunked");
								}
								else
								{
									logger.warning("Couldnt find Content-Length or Transfer-Encoding header in response");

									break;
								}


								// clear header buffer
								charBufferHeader.clear();


								// if transfer encoding is chunked and chunk
								// size is 0 try to read the next chunk size
								if(this.isChunked && this.chunkLength == 0)
								{
									int chunkLength = 0;

									for(int i = buffer.position(); i < buffer.limit(); i++)
									{
										bufferChunked.add(buffer.get(i));

										if(buffer.get(i) == 0xA && bufferChunked.containsRN())
										{
											chunkLength = bufferChunked.getChunkSize();

											this.chunkLength = chunkLength + 2;

											logger.info("Found next chunk size " + this.chunkLength);

											buffer.position(i + 1);

											break;
										}
									}
								}


								// read remaning bytes from buffer
								for(int i = buffer.position(); i < buffer.limit(); i++)
								{
									bufferBody.add(buffer.get(i));

									if(this.isContentLength && this.contentLength > 0)
									{
										this.contentLength--;
									}
									else if(this.isChunked && this.chunkLength > 0)
									{
										this.chunkLength--;
									}
								}

								logger.info("Read remaning " + buffer.remaining() + " bytes");
							}
						}
						else if(this.readChunkLength)
						{
							logger.info("Try to read next chunk size received " + buffer.limit() + " bytes");

							int chunkLength = 0;

							for(int i = 0; i < buffer.limit(); i++)
							{
								bufferChunked.add(buffer.get(i));

								if(buffer.get(i) == 0xA && bufferChunked.containsRN())
								{
									chunkLength = bufferChunked.getChunkSize();

									this.chunkLength = chunkLength + 2;
									this.readChunkLength = false;

									buffer.position(i + 1);

									logger.info("Found next chunk size " + this.chunkLength);

									break;
								}
							}


							// read remaning bytes from buffer
							if(chunkLength > 0)
							{
								for(int i = buffer.position(); i < buffer.limit() && this.chunkLength > 0; i++)
								{
									bufferBody.add(buffer.get(i));

									this.chunkLength--;
								}

								logger.info("Read remaning " + buffer.remaining() + " bytes");
							}
							else
							{
								this.chunkEnd = true;
							}
						}
						else
						{
							// add read
							for(int i = 0; i < buffer.limit(); i++)
							{
								bufferBody.add(buffer.get(i));

								if(this.isContentLength && this.contentLength > 0)
								{
									this.contentLength--;
								}
								else if(this.isChunked && this.chunkLength > 0)
								{
									this.chunkLength--;
								}
							}
						}


						if(this.isChunked)
						{
							if(this.chunkLength == 0 && this.readChunkLength == false)
							{
								this.readChunkLength = true;
							}

							logger.info("Current chunk size is " + this.chunkLength + " bytes");
						}


						// close channel
						if(this.isContentLength && this.contentLength == 0)
						{
							logger.info("Close channel because Content-Length was reached");

							channel.close();
						}
						else if(this.isChunked && this.chunkEnd)
						{
							logger.info("Close channel because chunk length was reached");

							channel.close();
						}
						else
						{
							if(buffer.remaining() == 0)
							{
								zeroRounds++;
							}
							else
							{
								zeroRounds = 0;
							}

							if(zeroRounds >= this.maxZeroRounds)
							{
								logger.info("Close connection because of " + zeroRounds + " zero rounds");

								channel.close();
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


			logger.info("Read body " + bufferBody.getSize() + " bytes");


			// create response
			this.response = new Response(this.header, bufferBody);


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
}
