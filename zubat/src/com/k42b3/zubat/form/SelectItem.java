package com.k42b3.zubat.form;

public class SelectItem 
{
	private String key;
	private String value;

	public SelectItem(String key, String value)
	{
		this.setKey(key);
		this.setValue(value);
	}

	public String getKey() 
	{
		return key;
	}

	public void setKey(String key) 
	{
		this.key = key;
	}

	public String getValue() 
	{
		return value;
	}

	public void setValue(String value) 
	{
		this.value = value;
	}
	
	public String toString()
	{
		return this.value;
	}
}
