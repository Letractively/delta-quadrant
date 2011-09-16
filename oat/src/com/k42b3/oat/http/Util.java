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

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Util
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Util 
{
	public final static String[] types = {"HTTP/1.0", "HTTP/1.1"};
	public final static String[] methods = {"OPTIONS", "GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "CONNECT"};
	public final static HashMap<Integer, String> codes = new HashMap<Integer, String>();
	
	public Util()
	{
		// 1xx
		codes.put(100, "Continue");
		codes.put(101, "Switching Protocols");
		codes.put(102, "Processing");
		// 2xx
		codes.put(200, "OK");
		codes.put(201, "Created");
		codes.put(202, "Accepted");
		codes.put(203, "Non-Authoritative Information");
		codes.put(204, "No Content");
		codes.put(205, "Reset Content");
		codes.put(206, "Partial Content");
		codes.put(207, "Multi-Status");
		// 3xx
		codes.put(300, "Multiple Choice");
		codes.put(301, "Moved Permanently");
		codes.put(302, "Found");
		codes.put(303, "See Other");
		codes.put(304, "Not Modified");
		codes.put(305, "Use Proxy");
		codes.put(306, "Switch Proxy");
		codes.put(307, "Temporary Redirect");
		// 4xx
		codes.put(400, "Bad Request");
		codes.put(401, "Unauthorized");
		codes.put(402, "Payment Required");
		codes.put(403, "Forbidden");
		codes.put(404, "Not Found");
		codes.put(405, "Method Not Allowed");
		codes.put(406, "Not Acceptable");
		codes.put(407, "Proxy Authentication Required");
		codes.put(408, "Request Time-out");
		codes.put(409, "Conflict");
		codes.put(410, "Gone");
		codes.put(411, "Length Required");
		codes.put(412, "Precondition Failed");
		codes.put(413, "Request Entity Too Large");
		codes.put(414, "Request-URI Too Long");
		codes.put(415, "Unsupported Media Type");
		codes.put(416, "Requested range not satisfiable");
		// 5xx
		codes.put(500, "Internal Server Error");
		codes.put(501, "Not Implemented");
		codes.put(502, "Bad Gateway");
		codes.put(503, "Service Unavailable");
		codes.put(504, "Gateway Timeout");
		codes.put(505, "HTTP Version Not Supported");
	}

	public static boolean isValidCode(int code, String message)
	{
		if(Util.codes.containsKey(code))
		{
			return Util.codes.get(code).equals(message);
		}

		return false;
	}

	public static boolean isValidMethod(String method)
	{
		for(int i = 0; i < Util.methods.length; i++)
		{
			if(Util.methods[i].equals(method))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isValidType(String type)
	{
		for(int i = 0; i < Util.types.length; i++)
		{
			if(Util.types[i].equals(type))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static Charset getContentTypeCharset(String contentType)
	{
		// default charset
		String charset = "UTF-8";

		
		// we look in the content-type header for an charset
		if(contentType != null)
		{
			int pos = contentType.indexOf("charset=");
			
			if(pos != -1)
			{
				charset = contentType.substring(pos + 8).trim();
			}
		}


		return Charset.forName(charset);
	}
	
	public static Map<String, String> parseHeader(String rawHeader, String delimiter)
	{
		LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
		
		String[] lines = rawHeader.split(delimiter);

		if(lines.length > 0)
		{
			// headers
			for(int i = 0; i < lines.length; i++)
			{
				int pos = lines[i].indexOf(':');
				
				if(pos != -1)
				{
					String key = lines[i].substring(0, pos).trim();
					String value = lines[i].substring(pos + 1).trim();

					if(!key.isEmpty() && !value.isEmpty())
					{
						headers.put(key, value);
					}
				}
			}
		}
		
		return headers;
	}
	
	public static String buildMessage(String statusLine, Map<String, String> header, String body, String delimter)
	{
		StringBuilder str = new StringBuilder();

		Iterator<Entry<String, String>> itr = header.entrySet().iterator();

		str.append(statusLine + delimter);

		while(itr.hasNext())
		{
			Entry<String, String> e = itr.next();

			str.append(e.getKey() + ": " + e.getValue() + delimter);
		}

		str.append(delimter);

		if(body != null && !body.isEmpty())
		{
			str.append(body);
		}

		return str.toString();
	}
	
	public static String urlEncode(String content)
	{
		try
		{
			return URLEncoder.encode(content, "UTF-8");
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
