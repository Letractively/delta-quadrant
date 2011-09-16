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

import java.util.Map;

/**
 * Message
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
	protected Buffer rawBody;

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

	public Buffer getRawBody()
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

	public void setRawBody(Buffer rawBody)
	{
		this.rawBody = rawBody;
	}
	
	public void setHeader(String key, String value)
	{
		this.header.put(key, value);
	}
}
