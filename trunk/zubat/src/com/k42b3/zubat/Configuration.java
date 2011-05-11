/**
 * zubat
 * 
 * An java application to access the API of amun. It is used to debug and
 * control a website based on amun. This is the reference implementation 
 * howto access the api. So feel free to hack and extend.
 * 
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of oat. oat is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * oat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with oat. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.zubat;

import java.io.File;

/**
 * Configuration
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Configuration 
{
	private String baseUrl = "http://127.0.0.1/projects/amun/public/";
	private String consumerKey = "b8858501073e5fb54e75b973ed044ec19f21a60d";
	private String consumerSecret = "07d8b5173afba2575e57ca5966624a39419b5b70";
	private String token = "cec314dc235df67c67b5c6b389b12102817135b2";
	private String tokenSecret = "5f9b76722e47b03bee7a91afb4946320b3ed4a28";

	public String getBaseUrl() 
	{
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) 
	{
		this.baseUrl = baseUrl;
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

	public String getToken() 
	{
		return token;
	}

	public void setToken(String token) 
	{
		this.token = token;
	}

	public String getTokenSecret() 
	{
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) 
	{
		this.tokenSecret = tokenSecret;
	}

	public static Configuration parseFile(File configFile)
	{
		Configuration config = new Configuration();

		String baseUrl = "http://127.0.0.1/projects/amun/public/";
		String consumerKey = "b8858501073e5fb54e75b973ed044ec19f21a60d";
		String consumerSecret = "07d8b5173afba2575e57ca5966624a39419b5b70";
		String token = "9782b074c5263a6de880310685558d8c8d00cc1e";
		String tokenSecret = "aea82b8e11530bc0dbdec718c4df935547efb2a5";

		config.setBaseUrl(baseUrl);
		config.setConsumerKey(consumerKey);
		config.setConsumerSecret(consumerSecret);
		config.setToken(token);
		config.setTokenSecret(tokenSecret);

		return config;
	}
}
