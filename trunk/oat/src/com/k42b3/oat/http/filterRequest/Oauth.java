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

package com.k42b3.oat.http.filterRequest;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.apache.commons.codec.digest.DigestUtils;

import com.k42b3.oat.RequestFilterInterface;
import com.k42b3.oat.http.Request;
import com.k42b3.oat.http.filterRequest.oauthSignature.SignatureInterface;
import com.k42b3.oat.http.filterRequest.oauthSignature.Util;

/**
 * oauth
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Oauth implements RequestFilterInterface
{
	private Properties config = new Properties();
	
	public void exec(Request request) 
	{
		try
		{
			// get config
			String consumer_key = this.config.getProperty("consumer_key");
			String consumer_secret = this.config.getProperty("consumer_secret");
			String token = this.config.getProperty("token");
			String token_secret = this.config.getProperty("token_secret");
			String method = this.config.getProperty("method");


			// add values
			HashMap<String, String> values = new HashMap<String, String>();
			
			values.put("oauth_consumer_key", consumer_key);
			values.put("oauth_token", token);
			values.put("oauth_signature_method", method);
			values.put("oauth_timestamp", this.get_timestamp());
			values.put("oauth_nonce", this.get_nonce());
			values.put("oauth_version", this.get_version());
			

			// add get vars to values
			values.putAll(request.get_params());


			// build base string
			String base_string = this.build_base_string(request.get_request_method(), request.get_url(), values);


			// get signature
			SignatureInterface sig;

			String cls = "com.k42b3.oat.http.filterRequest.oauthSignature." + method;

			Class c = Class.forName(cls);
			
			sig = (SignatureInterface) c.newInstance();


			// build signature
			values.put("oauth_signature", sig.build(base_string, consumer_secret, token_secret));
			
			
			// add header to request
			request.set_header("Authorization", "OAuth realm=\"oat\", " + this.build_auth_string(values));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private String build_auth_string(HashMap<String, String> values)
	{
		StringBuilder auth_string = new StringBuilder();

		Iterator<Entry<String, String>> it = values.entrySet().iterator();

		while(it.hasNext())
		{
			Entry<String, String> e = it.next();

			auth_string.append(Util.urlEncode(e.getKey()) + "=\"" + Util.urlEncode(e.getValue()) + "\", ");
		}

		String str = auth_string.toString();

		
		// remove ", " from string
		str = str.substring(0, str.length() - 2);

		
		return str;
	}

	private String build_base_string(String request_method, String url, HashMap<String, String> params)
	{
		StringBuilder base = new StringBuilder();
		
		base.append(Util.urlEncode(this.get_normalized_method(request_method)));

		base.append('&');
		
		base.append(Util.urlEncode(this.get_normalized_url(url)));

		base.append('&');
		
		base.append(Util.urlEncode(this.get_normalized_parameters(params)));

		return base.toString();
	}
	
	private String get_normalized_parameters(HashMap<String, String> params)
	{
		Iterator<Entry<String, String>> it = params.entrySet().iterator();

		List<String> keys = new ArrayList<String>();

		while(it.hasNext())
		{
			Entry<String, String> e = it.next();

			keys.add(e.getKey());
		}


		// sort params
		Collections.sort(keys);


		// build normalized params
		StringBuilder normalized_params = new StringBuilder();

		for(int i = 0; i < keys.size(); i++)
		{
			normalized_params.append(Util.urlEncode(keys.get(i)) + "=" + Util.urlEncode(params.get(keys.get(i))) + "&");
		}

		String str = normalized_params.toString();


		// remove trailing &
		str = str.substring(0, str.length() - 1);


		return str;
	}

	private String get_normalized_url(String raw_url)
	{
		try
		{
			raw_url = raw_url.toLowerCase();

			URL url = new URL(raw_url);

			int port = url.getPort();

			if(port == -1 || port == 80 || port == 443)
			{
				return url.getProtocol() + "://" + url.getHost() + "/" + url.getPath();
			}
			else
			{
				return url.getProtocol() + "://" + url.getHost() + ":" + port + "/" + url.getPath();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();

			return null;
		}
	}

	private String get_normalized_method(String method)
	{
		return method.toUpperCase();
	}

	private String get_timestamp()
	{
		return "" + (System.currentTimeMillis() / 1000);
	}

	private String get_nonce()
	{
		try
		{
			byte[] nonce = new byte[32];

			Random rand;

			rand = SecureRandom.getInstance("SHA1PRNG");

			rand.nextBytes(nonce);


			return DigestUtils.md5Hex(rand.toString());
		}
		catch(Exception e)
		{
			return DigestUtils.md5Hex("" + System.currentTimeMillis());
		}
	}

	private String get_version()
	{
		return "1.0";
	}

	public void set_config(Properties config)
	{
		this.config = config;
	}
}
