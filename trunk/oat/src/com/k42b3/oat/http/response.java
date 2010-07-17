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
		int pos = raw_response.indexOf("\n");
		
		if(pos == -1)
		{
			raw_line = raw_response.trim();
		}
		else
		{
			raw_line = raw_response.substring(0, pos).trim();
		}

		/*
		// split parts
		String[] parts = raw_line.split(" ");
		
		String type = "";
		int code = 0;
		String message = "";
		
		if(parts.length == 3)
		{
			type = parts[0];
			code = Integer.parseInt(parts[1]);
			message = parts[2];
		}
		else
		{
			type = "HTTP/1.1";
			code = 200;
			message = "OK";
		}
		
		
		// check method
		if(!util.is_valid_code(code, message))
		{
			code = 200;
			message = "OK";
		}
		
		
		// check type
		if(!util.is_valid_type(type))
		{
			type = http.type;
		}
		*/

		return raw_line;
	}
	
	public String toString()
	{
		return util.build_message(this.line, this.header, this.body);
	}
}
