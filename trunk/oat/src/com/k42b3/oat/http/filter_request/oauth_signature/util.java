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

package com.k42b3.oat.http.filter_request.oauth_signature;

import java.net.URLEncoder;
import java.security.MessageDigest;

import sun.misc.BASE64Encoder;

/**
 * util
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class util 
{
	public static String url_encode(String content)
	{
		try
		{
			String encoded = URLEncoder.encode(content);

			encoded = encoded.replaceAll("%7E", "~");
					
			return encoded;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static String base64_encode(byte[] content)
	{
		return (new BASE64Encoder()).encode(content);
	}
	
	public static String base64_encode(String content)
	{
		return util.base64_encode(content.getBytes());
	}

	public static String md5(byte[] content)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("MD5");
		
			digest.update(content);
					
			byte[] hash = digest.digest();

	        char buf[] = new char[hash.length * 2];
	        
	        char[] hex_chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',};

	        for(int i = 0, x = 0; i < hash.length; i++)
	        {
	            buf[x++] = hex_chars[(hash[i] >>> 4) & 0xf];
	            buf[x++] = hex_chars[hash[i] & 0xf];
	        }

	        return new String(buf);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static String md5(String content)
	{
		return util.md5(content.getBytes());
	}
}
