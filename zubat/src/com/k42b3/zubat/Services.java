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

import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Services
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Services
{
	private String baseUrl;
	private Http http;
	private Logger logger;

	private ArrayList<ServiceItem> services = new ArrayList<ServiceItem>();

	public Services(String baseUrl, Http http)
	{
		this.baseUrl = baseUrl;
		this.http = http;
		this.logger = Logger.getLogger("com.k42b3.zubat");
	}

	public void loadData() throws Exception
	{
		String url = this.getXrdsUrl(baseUrl);

		if(url != null)
		{
			this.request(url);
		}
		else
		{
			throw new Exception("Could not find xrds location");
		}
	}
	
	public Object getElementAt(int index) 
	{
		return services.get(index);
	}

	public int getSize() 
	{
		return services.size();
	}

	public ServiceItem getItem(String type)
	{
		for(int i = 0; i < services.size(); i++)
		{
			if(services.get(i).getType().equals(type))
			{
				return services.get(i);
			}
		}

		return null;
	}

	private String getXrdsUrl(String url) throws Exception
	{
		http.request(Http.GET, url, null, null, false);


		// find x-xrds-location header
		Header[] headers = http.getLastResponse().getAllHeaders();
		String xrdsLocation = null;

		for(int i = 0; i < headers.length; i++)
		{
			if(headers[i].getName().toLowerCase().equals("x-xrds-location"))
			{
				xrdsLocation = headers[i].getValue();

				break;
			}
		}


		return xrdsLocation;
	}

	private void request(String url) throws Exception
	{
		// request
		Document doc = http.requestXml(Http.GET, url);


		// parse services
		NodeList serviceList = doc.getElementsByTagName("Service");

		for(int i = 0; i < serviceList.getLength(); i++) 
		{
			Node serviceNode = serviceList.item(i);
			Element serviceElement = (Element) serviceNode;

			Element typeElement = (Element) serviceElement.getElementsByTagName("Type").item(0);
			Element uriElement = (Element) serviceElement.getElementsByTagName("URI").item(0);

			if(typeElement != null && uriElement != null)
			{
				String type = typeElement.getTextContent();
				String uri = uriElement.getTextContent();

				services.add(new ServiceItem(type, uri));
			}
		}

		logger.info("Found " + services.size() + " services");
	}
}
