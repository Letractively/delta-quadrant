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

import java.net.URL;
import java.util.HashMap;

/**
 * request
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class request extends message
{
	private URL url;
	private String host;
	private String path;
	private String method;

	public request(String url, String request) throws Exception
	{
		this.header = new HashMap<String, String>();
		
		this.parse_url(url);
		
		this.parse(request);
	}

	public String get_request_method()
	{
		return this.method;
	}

	public String get_url()
	{
		return this.url.toString();
	}

	public HashMap<String, String> get_params()
	{
		HashMap<String, String> params = new HashMap<String, String>();
		
		return params;
	}
	
	private void parse_url(String raw_url) throws Exception
	{
		if(!raw_url.startsWith("http://") && !raw_url.startsWith("https://"))
		{
			raw_url = "http://" + raw_url;
		}

		this.url = new URL(raw_url);
		
		this.host = url.getHost();
		this.path = url.getPath();
		
		if(this.url.getQuery() != null && !this.url.getQuery().isEmpty())
		{
			this.path+= "?" + this.url.getQuery();
		}
	}
	
	private void parse(String request)
	{
		// split header body
		String header = "";
		String body = "";

		int pos = request.indexOf(System.getProperty("line.separator") + System.getProperty("line.separator"));

		if(pos == -1)
		{
			header = request;
			body   = "";
		}
		else
		{
			header = request.substring(0, pos).trim();
			body   = request.substring(pos).trim();
		}


		// get request line
		this.set_line(this.parse_request_line(header));


		// parse header
		this.set_headers(util.parse_header(header, System.getProperty("line.separator")));

		this.set_header("Host", this.host);


		// set body
		this.set_body(body);
	}

	private String parse_request_line(String raw_request)
	{
		// get request line
		String raw_line;
		int pos = raw_request.indexOf(System.getProperty("line.separator"));
		
		if(pos == -1)
		{
			raw_line = raw_request.trim();
		}
		else
		{
			raw_line = raw_request.substring(0, pos).trim();
		}


		// split parts
		String[] parts = raw_line.split(" ");

		String method = "";
		String path = "";
		String type = "";
		
		if(parts.length == 3)
		{
			method = parts[0];
			path = parts[1];
			type = parts[2];
		}
		else if(parts.length == 2)
		{
			method = parts[0];
			path = parts[1];
			type = http.type;
		}
		else if(parts.length == 1)
		{
			method = parts[0];
			path = this.path;
			type = http.type;
		}
		else
		{
			method = http.method;
			path = this.path;
			type = http.type;
		}
		
		
		// check method
		if(!util.is_valid_method(method))
		{
			method = http.method;
		}
		
		
		// check path
		if(path.isEmpty())
		{
			path = "/";
		}
		
		if(!path.startsWith("/"))
		{
			path = "/" + path;
		}
		
		
		// check type
		if(!util.is_valid_type(type))
		{
			type = http.type;
		}
		
		
		this.method = method;


		return method + " " + path + " " + type;
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
