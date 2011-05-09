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

/**
 * OauthProvider
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class OauthProvider 
{
	private String requestUrl;
	private String authorizationUrl;
	private String accessUrl;

	private String consumerKey;
	private String consumerSecret;

	private String method = "HMAC-SHA1";

	public OauthProvider(String requestUrl, String authorizationUrl, String accessUrl, String consumerKey, String consumerSecret)
	{
		this.requestUrl = requestUrl;
		this.authorizationUrl = authorizationUrl;
		this.accessUrl = accessUrl;

		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
	}

	public String getRequestUrl() 
	{
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) 
	{
		this.requestUrl = requestUrl;
	}

	public String getAuthorizationUrl() 
	{
		return authorizationUrl;
	}

	public void setAuthorizationUrl(String authorizationUrl) 
	{
		this.authorizationUrl = authorizationUrl;
	}

	public String getAccessUrl() 
	{
		return accessUrl;
	}

	public void setAccessUrl(String accessUrl) 
	{
		this.accessUrl = accessUrl;
	}

	public String getConsumerKey() 
	{
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) 
	{
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() 
	{
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) 
	{
		this.consumerSecret = consumerSecret;
	}
	
	public String getMethod() 
	{
		return method;
	}

	public void setMethod(String method) 
	{
		this.method = method;
	}
}
