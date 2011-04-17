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

package com.k42b3.oat.http.filterResponse;

import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.util.Properties;

import com.k42b3.oat.ResponseFilterInterface;
import com.k42b3.oat.http.Response;

/**
 * charset
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Charset implements ResponseFilterInterface
{
	private Properties config = new Properties();
	
	public void exec(Response response) 
	{
		// default charset
		String charset_name = this.config.getProperty("charset");


		// we look in the content-type header for an charset
		String content_type = response.get_header().get("Content-Type");
		
		if(content_type != null)
		{
			int pos = content_type.indexOf("charset=");
			
			if(pos != -1)
			{
				charset_name = content_type.substring(pos + 8).trim();
			}
		}


		// get charset
		java.nio.charset.Charset charset;

		try
		{
			charset = java.nio.charset.Charset.forName(charset_name);
		}
		catch(Exception e)
		{
			charset = java.nio.charset.Charset.forName("UTF-8");
		}


		// decode
		CharsetDecoder decoder = charset.newDecoder();
		
		try
		{
			CharBuffer body = decoder.decode(response.get_raw_body());
			
			response.set_body(body.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void set_config(Properties config)
	{
		this.config = config;
	}
}
