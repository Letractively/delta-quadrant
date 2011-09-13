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
import java.util.Map;

/**
 * message
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Message 
{
	protected String line;
	protected Map<String, String> header;
	protected String body;
	protected ByteBuffer rawBody;

	public String getLine()
	{
		return this.line;
	}

	public Map<String, String> getHeader()
	{
		return this.header;
	}

	public String getBody()
	{
		return this.body;
	}

	public ByteBuffer getRawBody()
	{
		return this.rawBody;
	}

	public void setLine(String line)
	{
		this.line = line;
	}

	public void setHeaders(Map<String, String> headers)
	{
		this.header = headers;
	}

	public void setBody(String body)
	{
		this.body = body;
	}

	public void setRawBody(ByteBuffer rawBody)
	{
		this.rawBody = rawBody;
	}
	
	public void setHeader(String key, String value)
	{
		this.header.put(key, value);
	}
}
