package com.k42b3.kadabra;

import java.util.ArrayList;

public class HandlerFactory 
{
	public static HandlerAbstract factory(Resource resource, String path) throws Exception
	{
		if(resource.getType().equals("SYSTEM"))
		{
			return new com.k42b3.kadabra.handler.System(resource, path);
		}
		else if(resource.getType().equals("FTP"))
		{
			return new com.k42b3.kadabra.handler.Ftp(resource, path);
		}
		else if(resource.getType().equals("SSH"))
		{
			return new com.k42b3.kadabra.handler.Ssh(resource, path);
		}
		else
		{
			throw new Exception("Invalid resource type");
		}
	}

	public static ArrayList<String> factoryConfig(String type) throws Exception
	{
		if(type.equals("SYSTEM"))
		{
			return com.k42b3.kadabra.handler.System.getConfigFields();
		}
		else if(type.equals("FTP"))
		{
			return com.k42b3.kadabra.handler.Ftp.getConfigFields();
		}
		else if(type.equals("SSH"))
		{
			return com.k42b3.kadabra.handler.Ssh.getConfigFields();
		}
		else
		{
			throw new Exception("Invalid type");
		}
	}
}
