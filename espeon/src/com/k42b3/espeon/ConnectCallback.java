package com.k42b3.espeon;

public interface ConnectCallback 
{
	public void onConnect(String host, String db, String user, String pw) throws Exception;
}
