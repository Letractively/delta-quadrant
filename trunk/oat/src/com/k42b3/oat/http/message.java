package com.k42b3.oat.http;

import java.util.HashMap;

public class message 
{
	protected String line;
	protected HashMap<String, String> header;
	protected String body;
	
	public String get_line()
	{
		return this.line;
	}
	
	public HashMap<String, String> get_header()
	{
		return this.header;
	}
	
	public String get_body()
	{
		return this.body;
	}
		
	public void set_line(String line)
	{
		this.line = line;
	}
	
	public void set_header(HashMap<String, String> header)
	{
		this.header = header;
	}

	public void set_body(String body)
	{
		this.body = body;
	}
	
	public void add_header(String key, String value)
	{
		if(!this.header.containsKey(key))
		{
			this.header.put(key, value);
		}
	}
	
	public void replace_header(String key, String value)
	{
		if(this.header.containsKey(key))
		{
			this.header.put(key, value);
		}
	}
}
