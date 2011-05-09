/**
 * zubat
 * 
 * An java application to access the API of amun. It is used to debug and
 * control a website based on amun. This is the reference implementation 
 * howto access the api. So feel free to hack and extend.
 * 
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
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

package com.k42b3.zubat.oauth;

import java.nio.charset.Charset;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.k42b3.zubat.Oauth;

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
	public String build(String baseString, String consumerSecret, String tokenSecret)
	{
		try
		{
			String key = Oauth.urlEncode(consumerSecret) + "&" + Oauth.urlEncode(tokenSecret);


			Charset charset = Charset.defaultCharset();

			SecretKey sk = new SecretKeySpec(key.getBytes(charset), "HmacSHA1");

			Mac mac = Mac.getInstance("HmacSHA1");

			mac.init(sk);

			byte[] result = mac.doFinal(baseString.getBytes(charset));


			return Base64.encodeBase64String(result);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			return null;
		}
	}
}
