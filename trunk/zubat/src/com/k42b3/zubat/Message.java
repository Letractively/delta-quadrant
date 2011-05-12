package com.k42b3.zubat;

public class Message 
{
	private String text = "";
	private boolean success = true;

	public String getText() 
	{
		return text;
	}

	public void setText(String text) 
	{
		this.text = text;
	}

	public boolean getSuccess() 
	{
		return success;
	}

	public void setSuccess(boolean success) 
	{
		this.success = success;
	}
	
	public boolean hasSuccess()
	{
		return success;
	}
}
