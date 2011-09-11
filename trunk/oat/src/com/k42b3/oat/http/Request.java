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
public class Request extends Message
{
	private URL url;
	private String host;
	private String path;
	private String method;

	public Request(String url, String request) throws Exception
	{
		this.header = new HashMap<String, String>();
		
		this.parseUrl(url);
		
		this.parse(request);
	}

	public String getRequestMethod()
	{
		return this.method;
	}

	public String getUrl()
	{
		return this.url.toString();
	}

	public HashMap<String, String> getParams()
	{
		HashMap<String, String> params = new HashMap<String, String>();
		
		return params;
	}
	
	private void parseUrl(String rawUrl) throws Exception
	{
		if(!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://"))
		{
			rawUrl = "http://" + rawUrl;
		}

		this.url = new URL(rawUrl);
		
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

		int pos = request.indexOf("\n\n");

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
		this.setLine(this.parseRequestLine(header));


		// parse header
		this.setHeaders(Util.parseHeader(header, "\n"));

		this.setHeader("Host", this.host);


		// set body
		this.setBody(body);
	}

	private String parseRequestLine(String rawRequest)
	{
		String rawLine;
		int pos = rawRequest.indexOf("\n");
		
		if(pos == -1)
		{
			rawLine = rawRequest.trim();
		}
		else
		{
			rawLine = rawRequest.substring(0, pos).trim();
		}


		// split parts
		String[] parts = rawLine.split(" ");

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
			type = Http.type;
		}
		else if(parts.length == 1)
		{
			method = parts[0];
			path = this.path;
			type = Http.type;
		}
		else
		{
			method = Http.method;
			path = this.path;
			type = Http.type;
		}
		
		
		// check method
		if(!Util.isValidMethod(method))
		{
			method = Http.method;
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
		if(!Util.isValidType(type))
		{
			type = Http.type;
		}
		
		
		this.method = method;


		return method + " " + path + " " + type;
	}

	public String toString()
	{
		return Util.buildMessage(this.line, this.header, this.body, "\n");
	}

	public String getHttpMessage()
	{
		return Util.buildMessage(this.line, this.header, this.body, Http.newLine);
	}
}
