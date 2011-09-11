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
public class Response extends Message
{
	public Response(String header, ByteBuffer rawBody)
	{
		// get request line
		this.setLine(this.parseResponseLine(header));
		
		
		// parse header
		this.setHeaders(Util.parseHeader(header, Http.newLine));
		
		
		// set raw body
		this.setRawBody(rawBody);


		// set body in hex format
		StringBuilder body = new StringBuilder();
		String hex;
		int i = 0;

		rawBody.rewind();

		while(i < rawBody.remaining())
		{
			if(i > 0 && i % 16 == 0)
			{
				body.append("\n");
			}
			
			
			hex = (Integer.toHexString(rawBody.get(i))).toUpperCase();
			
			if(hex.length() == 1)
			{
				hex = "0" + hex;
			}
			
			body.append(hex + " ");
			
			i++;
		}
		
		this.setBody(body.toString());
	}

	private String parseResponseLine(String rawResponse)
	{
		// get response line
		String rawLine;
		int pos = rawResponse.indexOf(Http.newLine);
		
		if(pos == -1)
		{
			rawLine = rawResponse.trim();
		}
		else
		{
			rawLine = rawResponse.substring(0, pos).trim();
		}

		return rawLine;
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
