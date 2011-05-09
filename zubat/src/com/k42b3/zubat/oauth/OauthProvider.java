package com.k42b3.zubat.oauth;

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
