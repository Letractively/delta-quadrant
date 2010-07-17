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

package com.k42b3.oat.http.filter_response;

import java.util.Properties;
import java.util.zip.Inflater;

import com.k42b3.oat.iresponse_filter;
import com.k42b3.oat.http.response;

/**
 * deflate
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class deflate implements iresponse_filter
{
	private Properties config = new Properties();
	
	public void exec(response response) 
	{
		if(response.get_header().containsKey("Content-Encoding"))
		{
			String encoding = response.get_header().get("Content-Encoding");
			
			if(encoding.indexOf("deflate") != -1)
			{
				try
				{
					Inflater inf = new Inflater();

					inf.setInput(response.get_body().getBytes());


					// decode
					StringBuilder buffer = new StringBuilder();
					byte[] buf = new byte[512];

					while(inf.inflate(buf) > 0)
					{
						buffer.append(buf);
					}
					
					
					response.set_body(buffer.toString());
				}
				catch(Exception e)
				{
				}
			}
		}
	}
	
	public void set_config(Properties config)
	{
		this.config = config;
	}
}
