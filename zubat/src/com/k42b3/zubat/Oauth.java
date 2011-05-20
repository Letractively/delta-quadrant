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

package com.k42b3.zubat;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.k42b3.zubat.oauth.OauthProvider;
import com.k42b3.zubat.oauth.SignatureInterface;

/**
 * Oauth
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Oauth 
{
	private OauthProvider provider;
	
	private String token;
	private String tokenSecret;
	private boolean callbackConfirmed;
	private String verificationCode;

	private Logger logger;
	private boolean authed = false;
	private TrafficListenerInterface trafficListener;

	public Oauth(OauthProvider provider)
	{
		this.provider = provider;
		this.logger = Logger.getLogger("com.k42b3.zubat");
	}

	public Oauth(OauthProvider provider, TrafficListenerInterface trafficListener)
	{
		this(provider);

		this.trafficListener = trafficListener;
	}

	public void auth(String token, String tokenSecret)
	{
		this.setToken(token);
		this.setTokenSecret(tokenSecret);
		this.authed = true;
	}

	public void setToken(String token)
	{
		this.token = token;
	}
	
	public void setTokenSecret(String tokenSecret)
	{
		this.tokenSecret = tokenSecret;
	}

	public String getToken()
	{
		return token;
	}

	public String getTokenSecret()
	{
		return tokenSecret;
	}

	public boolean isAuthed()
	{
		return authed;
	}

	public boolean requestToken()
	{
		try
		{
			// add values
			HashMap<String, String> values = new HashMap<String, String>();

			String requestMethod = "GET";

			values.put("oauth_consumer_key", this.provider.getConsumerKey());
			values.put("oauth_signature_method", provider.getMethod());
			values.put("oauth_timestamp", this.getTimestamp());
			values.put("oauth_nonce", this.getNonce());
			values.put("oauth_version", this.getVersion());
			values.put("oauth_callback", "oob");


			// add get vars to values
			URL requestUrl = new URL(provider.getRequestUrl());
			values.putAll(getQueryMap(requestUrl.getQuery()));


			// build base string
			String baseString = this.buildBaseString(requestMethod, provider.getRequestUrl(), values);


			// get signature
			SignatureInterface signature = this.getSignature();

			if(signature == null)
			{
				throw new Exception("Invalid signature method");
			}


			// build signature
			values.put("oauth_signature", signature.build(baseString, provider.getConsumerSecret(), ""));


			// add header to request
			HashMap<String, String> header = new HashMap<String, String>();
			header.put("Authorization", "OAuth realm=\"zubat\", " + this.buildAuthString(values));

			HttpEntity entity = this.httpRequest(requestMethod, provider.getRequestUrl(), header, "");


			// parse response
			this.token = null;
			this.tokenSecret = null;
			this.callbackConfirmed = false;

			List<NameValuePair> pairs = URLEncodedUtils.parse(entity);

			for(int i = 0; i < pairs.size(); i++)
			{
				if(pairs.get(i).getName().equals("oauth_token"))
				{
					this.token = pairs.get(i).getValue();

					logger.info("Received token: " + this.token);
				}

				if(pairs.get(i).getName().equals("oauth_token_secret"))
				{
					this.tokenSecret = pairs.get(i).getValue();

					logger.info("Received token secret: " + this.tokenSecret);
				}

				if(pairs.get(i).getName().equals("oauth_callback_confirmed"))
				{
					this.callbackConfirmed = pairs.get(i).getValue().equals("1");
				}
			}

			if(this.token == null)
			{
				throw new Exception("No oauth token received");
			}

			if(this.tokenSecret == null)
			{
				throw new Exception("No oauth token secret received");
			}

			if(this.callbackConfirmed != true)
			{
				throw new Exception("Callback was not confirmed");
			}
			
			return true;
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
			
			return false;
		}
	}

	public boolean authorizeToken()
	{
		try
		{
			String url;

			if(this.provider.getAuthorizationUrl().indexOf('?') == -1)
			{
				url = this.provider.getAuthorizationUrl() + "?oauth_token=" + this.token;
			}
			else
			{
				url = this.provider.getAuthorizationUrl() + "&oauth_token=" + this.token;
			}

			URI authUrl = new URI(url);

			if(Desktop.isDesktopSupported())
			{
				Desktop desktop = Desktop.getDesktop();

				if(desktop.isSupported(Desktop.Action.BROWSE))
				{
					desktop.browse(authUrl);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Visit the following URL: " + authUrl);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Visit the following URL: " + authUrl);
			}

			verificationCode = JOptionPane.showInputDialog("Please enter the verification Code");

			return true;
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
			
			return false;
		}
	}

	public boolean accessToken()
	{
		try
		{
			// add values
			HashMap<String, String> values = new HashMap<String, String>();

			String requestMethod = "GET";

			values.put("oauth_consumer_key", this.provider.getConsumerKey());
			values.put("oauth_token", this.token);
			values.put("oauth_signature_method", provider.getMethod());
			values.put("oauth_timestamp", this.getTimestamp());
			values.put("oauth_nonce", this.getNonce());
			values.put("oauth_verifier", this.verificationCode);


			// add get vars to values
			URL accessUrl = new URL(provider.getAccessUrl());
			values.putAll(getQueryMap(accessUrl.getQuery()));


			// build base string
			String baseString = this.buildBaseString(requestMethod, provider.getAccessUrl(), values);


			// get signature
			SignatureInterface signature = this.getSignature();

			if(signature == null)
			{
				throw new Exception("Invalid signature method");
			}


			// build signature
			values.put("oauth_signature", signature.build(baseString, provider.getConsumerSecret(), this.tokenSecret));


			// add header to request
			HashMap<String, String> header = new HashMap<String, String>();
			header.put("Authorization", "OAuth realm=\"zubat\", " + this.buildAuthString(values));

			HttpEntity entity = this.httpRequest(requestMethod, provider.getAccessUrl(), header, "");


			// parse response
			this.token = null;
			this.tokenSecret = null;

			List<NameValuePair> pairs = URLEncodedUtils.parse(entity);

			for(int i = 0; i < pairs.size(); i++)
			{
				if(pairs.get(i).getName().equals("oauth_token"))
				{
					this.token = pairs.get(i).getValue();

					logger.info("Received token: " + this.token);
				}

				if(pairs.get(i).getName().equals("oauth_token_secret"))
				{
					this.tokenSecret = pairs.get(i).getValue();

					logger.info("Received token secret: " + this.tokenSecret);
				}
			}

			if(this.token == null)
			{
				throw new Exception("No oauth token received");
			}

			if(this.tokenSecret == null)
			{
				throw new Exception("No oauth token secret received");
			}

			return true;
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
			
			return false;
		}
	}

	public void signRequest(HttpRequestBase request)
	{
		try
		{
			// add values
			HashMap<String, String> values = new HashMap<String, String>();
			HashMap<String, String> auth;

			values.put("oauth_consumer_key", this.provider.getConsumerKey());
			values.put("oauth_token", this.token);
			values.put("oauth_signature_method", provider.getMethod());
			values.put("oauth_timestamp", this.getTimestamp());
			values.put("oauth_nonce", this.getNonce());


			auth = (HashMap<String, String>) values.clone();


			// add get vars to values
			values.putAll(getQueryMap(request.getURI().getQuery()));


			// build base string
			String baseString = this.buildBaseString(request.getMethod(), request.getURI().toString(), values);


			// get signature
			SignatureInterface signature = this.getSignature();

			if(signature == null)
			{
				throw new Exception("Invalid signature method");
			}


			// build signature
			auth.put("oauth_signature", signature.build(baseString, provider.getConsumerSecret(), this.tokenSecret));


			// add header to request
			request.addHeader("Authorization", "OAuth realm=\"zubat\", " + this.buildAuthString(auth));
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}
	}

	private String buildAuthString(HashMap<String, String> values)
	{
		StringBuilder authString = new StringBuilder();

		Iterator<Entry<String, String>> it = values.entrySet().iterator();

		while(it.hasNext())
		{
			Entry<String, String> e = it.next();

			authString.append(urlEncode(e.getKey()) + "=\"" + urlEncode(e.getValue()) + "\", ");
		}

		String str = authString.toString();


		// remove ", " from string
		str = str.substring(0, str.length() - 2);


		return str;
	}

	private String buildBaseString(String requestMethod, String url, HashMap<String, String> params)
	{
		StringBuilder base = new StringBuilder();

		base.append(urlEncode(this.getNormalizedMethod(requestMethod)));

		base.append('&');
		
		base.append(urlEncode(this.getNormalizedUrl(url)));

		base.append('&');
		
		base.append(urlEncode(this.getNormalizedParameters(params)));

		return base.toString();
	}
	
	private String getNormalizedParameters(HashMap<String, String> params)
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
		StringBuilder normalizedParams = new StringBuilder();

		for(int i = 0; i < keys.size(); i++)
		{
			normalizedParams.append(urlEncode(keys.get(i)) + "=" + urlEncode(params.get(keys.get(i))) + "&");
		}

		String str = normalizedParams.toString();


		// remove trailing &
		str = str.substring(0, str.length() - 1);


		return str;
	}

	private String getNormalizedUrl(String rawUrl)
	{
		try
		{
			rawUrl = rawUrl.toLowerCase();

			URL url = new URL(rawUrl);

			int port = url.getPort();

			if(port == -1 || port == 80 || port == 443)
			{
				return url.getProtocol() + "://" + url.getHost() + url.getPath();
			}
			else
			{
				return url.getProtocol() + "://" + url.getHost() + ":" + port + url.getPath();
			}
		}
		catch(Exception e)
		{
			Zubat.handleException(e);

			return null;
		}
	}

	private String getNormalizedMethod(String method)
	{
		return method.toUpperCase();
	}

	private String getTimestamp()
	{
		return "" + (System.currentTimeMillis() / 1000);
	}

	private String getNonce()
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

	private String getVersion()
	{
		return "1.0";
	}

	private HttpEntity httpRequest(String method, String url, Map<String, String> header, String body)
	{
		try
		{
			// build request
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);


			HttpRequestBase request;

			if(method.equals("GET"))
			{
				request = new HttpGet(url);
			}
			else if(method.equals("POST"))
			{
				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				entity.addPart("text", new StringBody(body)); 

				request = new HttpPost(url);

				((HttpPost) request).setEntity(entity);
			}
			else
			{
				throw new Exception("Invalid request method");
			}


			// header
			Set<String> keys = header.keySet();

			for(String key : keys)
			{
				request.setHeader(key, header.get(key));
			}


	        // execute HTTP Get Request
			logger.info("Request: " + request.getRequestLine());

			HttpResponse httpResponse = httpClient.execute(request);

			HttpEntity entity = httpResponse.getEntity();

			String responseContent = EntityUtils.toString(entity);
			

			// log traffic
			if(trafficListener != null)
			{
				TrafficItem trafficItem = new TrafficItem();

				trafficItem.setRequest(request);
				trafficItem.setResponse(httpResponse);
				trafficItem.setResponseContent(responseContent);

				trafficListener.handleRequest(trafficItem);
			}


			// check status code
			int statusCode = httpResponse.getStatusLine().getStatusCode();

			if(!(statusCode >= 200 && statusCode < 300))
			{
				JOptionPane.showMessageDialog(null, responseContent);

				throw new Exception("No successful status code");
			}


			return entity;
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
			
			return null;
		}
	}

	private SignatureInterface getSignature()
	{
		try
		{
			String cls;

			if(provider.getMethod().equals("HMAC-SHA1"))
			{
				cls = "com.k42b3.zubat.oauth.HMACSHA1";
			}
			else if(provider.getMethod().equals("PLAINTEXT"))
			{
				cls = "com.k42b3.zubat.oauth.PLAINTEXT";
			}
			else
			{
				throw new Exception("Invalid signature method");
			}

			return (SignatureInterface) Class.forName(cls).newInstance();
		}
		catch(Exception e)
		{
			Zubat.handleException(e);

			return null;
		}
	}

	public static String urlEncode(String content)
	{
		try
		{
			if(!content.isEmpty())
			{
				String encoded = URLEncoder.encode(content, "UTF8");

				encoded = encoded.replaceAll("%7E", "~");

				return encoded;
			}
			else
			{
				return "";
			}
		}
		catch(Exception e)
		{
			return "";
		}
	}

	public static Map<String, String> getQueryMap(String query)
	{
		Map<String, String> map = new HashMap<String, String>();
		
		if(query != null)
		{
			String[] params = query.split("&");

			for(String param : params)
			{
				String[] pair = param.split("=");

				if(pair.length >= 1)
				{
					String name  = pair[0];
					String value = pair.length == 2 ? pair[1] : "";

					map.put(name, value);
				}
			}
		}

		return map;
	}
}
