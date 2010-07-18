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

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * message
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class message 
{
	protected String line;
	protected HashMap<String, String> header;
	protected String body;
	protected ByteBuffer raw_body;
	
	public String get_line()
	{
		return this.line;
	}
	
	public HashMap<String, String> get_header()
	{
		return this.header;
	}
	
	public String get_body()
	{
		return this.body;
	}
		
	public ByteBuffer get_raw_body()
	{
		return this.raw_body;
	}
	
	public void set_line(String line)
	{
		this.line = line;
	}
	
	public void set_headers(HashMap<String, String> headers)
	{
		this.header = headers;
	}

	public void set_body(String body)
	{
		this.body = body;
	}

	public void set_raw_body(ByteBuffer raw_body)
	{
		this.raw_body = raw_body;
	}

	public void add_header(String key, String value)
	{
		if(!this.header.containsKey(key))
		{
			this.header.put(key, value);
		}
	}
	
	public void set_header(String key, String value)
	{
		this.header.put(key, value);
	}
	
	public void replace_header(String key, String value)
	{
		if(this.header.containsKey(key))
		{
			this.header.put(key, value);
		}
	}
}
