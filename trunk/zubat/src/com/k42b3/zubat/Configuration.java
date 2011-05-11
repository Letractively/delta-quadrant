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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
	private String baseUrl;
	private String consumerKey;
	private String consumerSecret;
	private String token;
	private String tokenSecret;

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

	public static Configuration parseFile(File configFile) throws Exception
	{
		Configuration config = new Configuration();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(configFile);

		Element rootElement = (Element) doc.getDocumentElement();

		rootElement.normalize();


		// parse config elements
		String baseUrl = "";
		String consumerKey = "";
		String consumerSecret = "";
		String token = "";
		String tokenSecret = "";

		Element baseUrlElement = (Element) doc.getElementsByTagName("baseUrl").item(0);
		Element consumerKeyElement = (Element) doc.getElementsByTagName("consumerKey").item(0);
		Element consumerSecretElement = (Element) doc.getElementsByTagName("consumerSecret").item(0);
		Element tokenElement = (Element) doc.getElementsByTagName("token").item(0);
		Element tokenSecretElement = (Element) doc.getElementsByTagName("tokenSecret").item(0);
		
		if(baseUrlElement != null)
		{
			baseUrl = baseUrlElement.getTextContent();
		}
		else
		{
			throw new Exception("baseUrl in config not set");
		}
		
		if(consumerKeyElement != null)
		{
			consumerKey = consumerKeyElement.getTextContent();
		}
		else
		{
			throw new Exception("consumerKey in config not set");
		}
		
		if(consumerSecretElement != null)
		{
			consumerSecret = consumerSecretElement.getTextContent();
		}
		else
		{
			throw new Exception("consumerSecret in config not set");
		}

		if(tokenElement != null)
		{
			token = tokenElement.getTextContent();
		}

		if(tokenSecretElement != null)
		{
			tokenSecret = tokenSecretElement.getTextContent();
		}

		config.setBaseUrl(baseUrl);
		config.setConsumerKey(consumerKey);
		config.setConsumerSecret(consumerSecret);
		config.setToken(token);
		config.setTokenSecret(tokenSecret);

		return config;
	}
}
