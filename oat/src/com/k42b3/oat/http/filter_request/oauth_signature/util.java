package com.k42b3.oat.http.filter_request.oauth_signature;

import java.security.MessageDigest;

public class util 
{
	public static String base64_encode(String content)
	{
		return "";
	}

	public static String md5(String content)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("MD5");
		
			digest.update(content.getBytes());
					
			byte[] hash = digest.digest();

	        char buf[] = new char[hash.length * 2];
	        
	        char[] hex_chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',};

	        for(int i = 0, x = 0; i < hash.length; i++)
	        {
	            buf[x++] = hex_chars[(hash[i] >>> 4) & 0xf];
	            buf[x++] = hex_chars[hash[i] & 0xf];
	        }

	        return new String(buf);
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
