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

package com.k42b3.oat.http.filterRequest.oauthSignature;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * HMACSHA1
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class HMACSHA1 implements SignatureInterface
{
	public String build(String base_string, String consumer_secret, String token_secret)
	{
		try
		{
			String key = Util.urlEncode(consumer_secret) + "&" + Util.urlEncode(token_secret);


			KeyGenerator kg = KeyGenerator.getInstance("HmacSHA1");

			SecretKey sk = kg.generateKey();

			Mac mac = Mac.getInstance("HmacSHA1");

			mac.init(sk);

			byte[] result = mac.doFinal(key.getBytes());


			return Base64.encode(result);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return null;
		}
	}
}
