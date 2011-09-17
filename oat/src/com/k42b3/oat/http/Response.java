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

import java.nio.ByteBuffer;

/**
 * Response
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Response extends Message
{
	public Response(String header, Buffer bufferBody)
	{
		// get request line
		this.setLine(this.parseResponseLine(header));


		// parse header
		this.setHeaders(Util.parseHeader(header, Http.newLine));


		// set raw body
		this.setRawBody(bufferBody);


		// set body in hex format
		StringBuilder body = new StringBuilder();
		StringBuilder bodyFormat = new StringBuilder();
		String hex;
		int i = 0;

		ByteBuffer rawBody = bufferBody.getByteBuffer();
		rawBody.rewind();

		while(i < rawBody.remaining())
		{
			hex = (Integer.toHexString(rawBody.get(i))).toUpperCase();

			body.append(hex);

			i++;
		}

		for(int j = 0; j < body.length(); j++)
		{
			if(j > 0 && j % 32 == 0)
			{
				bodyFormat.append("\n");
			}
			else if (j > 0 && j % 2 == 0)
			{
				bodyFormat.append(" ");
			}

			bodyFormat.append(body.charAt(j));
		}


		this.setBody(bodyFormat.toString());
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
