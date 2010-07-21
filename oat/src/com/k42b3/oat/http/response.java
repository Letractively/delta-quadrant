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
	public response(String response)
	{
		this.parse(response);
	}

	public String get_charset()
	{
		// default charset
		String charset = "UTF-8";

		
		// we look in the content-type header for an charset
		String content_type = this.get_header().get("Content-Type");
		
		if(content_type != null)
		{
			int pos = content_type.indexOf("charset=");
			
			if(pos != -1)
			{
				charset = content_type.substring(pos + 8).trim();
			}
		}


		// map aliases
		charset = charset.toUpperCase();
		
		if(charset.equals("ISO-8859-1"))
		{
			charset = "ISO8859_1";
		}
		else if(charset.equals("UTF-8"))
		{
			charset = "UTF8";
		}
		else if(charset.equals("US-ASCII"))
		{
			charset = "ASCII";
		}
	

		return charset;
	}
	
	private void parse(String response)
	{
		// split header body
		String header = "";
		String body = "";

		int pos = response.indexOf(http.new_line + http.new_line);

		if(pos == -1)
		{
			header = response;
			body   = "";
		}
		else
		{
			header = response.substring(0, pos).trim();
			body   = response.substring(pos + (http.new_line + http.new_line).length());
		}
		
		
		// get request line
		this.set_line(this.parse_response_line(header));
		
		
		// parse header
		this.set_headers(util.parse_header(header, http.new_line));


		// set body
		this.set_body(body);
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
		return util.build_message(this.line, this.header, this.body, System.getProperty("line.separator"));
	}

	public String get_http_message()
	{
		return util.build_message(this.line, this.header, this.body, http.new_line);
	}
}
