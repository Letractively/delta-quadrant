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

package com.k42b3.oat.http.filter_request;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import com.k42b3.oat.irequest_filter;
import com.k42b3.oat.http.request;
import com.k42b3.oat.http.filter_request.oauth_signature.isignature;
import com.k42b3.oat.http.filter_request.oauth_signature.util;

/**
 * oauth
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class oauth implements irequest_filter
{
	private Properties config = new Properties();
	
	public void exec(request request) 
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
			isignature sig;
			
			String cls = "com.k42b3.oat.http.filter_request.oauth_signature." + method;
			
			Class c = Class.forName(cls);
			
			sig = (isignature) c.newInstance();


			// build signature
			values.put("oauth_signature", sig.build(base_string, consumer_secret, token_secret));
			
			
			// add header to request
			request.set_header("Authorization", "OAuth realm=\"oat\", " + this.build_auth_string(values));
		}
		catch(Exception e)
		{
		}
	}

	private String build_auth_string(HashMap<String, String> values)
	{
		return "";
	}

	private String build_base_string(String request_method, String url, HashMap<String, String> params)
	{
		StringBuilder base = new StringBuilder();
		
		base.append(this.url_encode(this.get_normalized_method(request_method)));

		base.append('&');
		
		base.append(this.url_encode(this.get_normalized_url(url)));

		base.append('&');
		
		base.append(this.url_encode(this.get_normalized_parameters(params)));

		return base.toString();
	}
	
	private String get_normalized_parameters(HashMap<String, String> params)
	{
		return "";
	}
	
	private String get_normalized_url(String url)
	{
		return "";
	}
	
	private String get_normalized_method(String method)
	{
		return method.toUpperCase();
	}
	
	private String url_encode(String content)
	{
		return "";
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
			
			return util.md5(rand.toString());
		}
		catch(Exception e)
		{
			return util.md5("" + System.currentTimeMillis());
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
