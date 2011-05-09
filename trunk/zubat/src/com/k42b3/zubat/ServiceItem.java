package com.k42b3.zubat;

public class ServiceItem 
{
	private String type;
	private String uri;

	public ServiceItem(String type, String uri)
	{
		this.setType(type);
		this.setUri(uri);
	}

	public String getType() 
	{
		return type;
	}

	public void setType(String type) 
	{
		this.type = type;
	}

	public String getUri() 
	{
		return uri;
	}

	public void setUri(String uri) 
	{
		this.uri = uri;
	}
	
	public String toString()
	{
		return type;
	}
}
