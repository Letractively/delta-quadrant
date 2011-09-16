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

package com.k42b3.oat.filter.response;

import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.util.Properties;

import com.k42b3.oat.filter.ResponseFilterInterface;
import com.k42b3.oat.http.Response;

/**
 * Charset
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Charset implements ResponseFilterInterface
{
	private Properties config = new Properties();
	
	public void exec(Response response) throws Exception
	{
		// default charset
		String charsetName = this.config.getProperty("charset");


		// we look in the content-type header for an charset
		String contentType = response.getHeader().get("Content-Type");
		
		if(contentType != null)
		{
			int pos = contentType.indexOf("charset=");
			
			if(pos != -1)
			{
				charsetName = contentType.substring(pos + 8).trim();
			}
		}


		// get charset
		java.nio.charset.Charset charset;

		try
		{
			charset = java.nio.charset.Charset.forName(charsetName);
		}
		catch(Exception e)
		{
			charset = java.nio.charset.Charset.forName("UTF-8");
		}


		// decode
		CharsetDecoder decoder = charset.newDecoder();

		CharBuffer body = decoder.decode(response.getRawBody().getByteBuffer());

		response.setBody(body.toString());
	}

	public void setConfig(Properties config)
	{
		this.config = config;
	}
}