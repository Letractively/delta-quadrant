package com.k42b3.neodym;

import java.util.Date;

/**
 * Represents an cached http request and response
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Cache 
{
	private String key;
	private String response;
	private Date expire;

	public Cache(String key, String response, Date expire)
	{
		this.key = key;
		this.response = response;
		this.expire = expire;
	}

	public String getKey() 
	{
		return key;
	}

	public void setKey(String key) 
	{
		this.key = key;
	}

	public String getResponse() 
	{
		return response;
	}

	public void setResponse(String response) 
	{
		this.response = response;
	}

	public Date getExpire() 
	{
		return expire;
	}

	public void setExpire(Date expire) 
	{
		this.expire = expire;
	}
}
