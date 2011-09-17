package com.k42b3.oat.filter;

import java.util.Properties;
import java.util.logging.Logger;

abstract public class FilterAbstract 
{
	protected Properties config = new Properties();

	protected Logger logger = Logger.getLogger("com.k42b3.oat");

	public String getName()
	{
		return this.getClass().getName();
	}

	public void setConfig(Properties config)
	{
		this.config = config;
	}
	
	public Properties getConfig()
	{
		return this.config;
	}
}
