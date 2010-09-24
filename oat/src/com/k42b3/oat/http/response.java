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

import java.nio.ByteBuffer;

/**
 * response
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class response extends message
{
	public response(String header, ByteBuffer raw_body)
	{
		// get request line
		this.set_line(this.parse_response_line(header));
		
		
		// parse header
		this.set_headers(util.parse_header(header, http.new_line));
		
		
		// set raw body
		this.set_raw_body(raw_body);


		// set body in hex format
		StringBuilder body = new StringBuilder();
		String hex;
		int i = 0;

		raw_body.rewind();

		while(i < raw_body.remaining())
		{
			if(i > 0 && i % 16 == 0)
			{
				body.append("\n");
			}
			
			
			hex = (Integer.toHexString(raw_body.get(i))).toUpperCase();
			
			if(hex.length() == 1)
			{
				hex = "0" + hex;
			}
			
			body.append(hex + " ");
			
			i++;
		}
		
		this.set_body(body.toString());
	}

	private String parse_response_line(String raw_response)
	{
		// get response line
		String raw_line;
		int pos = raw_response.indexOf(http.new_line);
		
		if(pos == -1)
		{
			raw_line = raw_response.trim();
		}
		else
		{
			raw_line = raw_response.substring(0, pos).trim();
		}

		return raw_line;
	}
	
	public String toString()
	{
		return util.build_message(this.line, this.header, this.body, "\n");
	}

	public String get_http_message()
	{
		return util.build_message(this.line, this.header, this.body, http.new_line);
	}
}
