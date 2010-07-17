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
import java.util.Set;

import com.k42b3.oat.icallback;
import com.k42b3.oat.iresponse_filter;

/**
 * http
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
	private StringBuilder raw_response;
	
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
			this.raw_response = new StringBuilder();

			SocketChannel channel = null;


			InetSocketAddress socket_address = new InetSocketAddress(this.host, this.port);


			Charset charset = Charset.forName("UTF-8");
			CharsetDecoder decoder = charset.newDecoder();
			CharsetEncoder encoder = charset.newEncoder();

			
			// allocate buffer
			ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
			CharBuffer char_buffer = CharBuffer.allocate(1024);


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

						
						decoder.decode(buffer, char_buffer, false);

						char_buffer.flip();


						// if buffer is empty finish
						if(char_buffer.length() == 0)
						{
							key.cancel();
							//channel.close();
						}


						// append text
						raw_response.append(char_buffer);


						// clear buffer
						buffer.clear();
						
						char_buffer.clear();
					}
					else if(key.isWritable())
					{
						key_channel.write(encoder.encode(CharBuffer.wrap(request.toString())));
						
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

			this.response = new response(this.raw_response.toString());


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
