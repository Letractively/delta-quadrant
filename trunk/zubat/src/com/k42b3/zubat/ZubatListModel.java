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

import javax.swing.AbstractListModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ZubatListModel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class ZubatListModel extends AbstractListModel
{
	private Logger logger;

	private ArrayList<ServiceItem> services = new ArrayList<ServiceItem>();

	public ZubatListModel(String baseUrl) throws Exception
	{
		this.logger = Logger.getLogger("com.k42b3.zubat");

		this.request(baseUrl + "api/meta/xrds");
	}

	public Object getElementAt(int index) 
	{
		return services.get(index);
	}

	public int getSize() 
	{
		return services.size();
	}

	public String getUri(String type)
	{
		for(int i = 0; i < services.size(); i++)
		{
			if(services.get(i).getType().equals(type))
			{
				return services.get(i).getUri();
			}
		}

		return null;
	}

	private void request(String url) throws Exception
	{
		// build request
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

		HttpGet getRequest = new HttpGet(url);

		Zubat.getOauth().signRequest(getRequest);

		logger.info("Request: " + getRequest.getRequestLine());

		HttpResponse httpResponse = httpClient.execute(getRequest);

		HttpEntity entity = httpResponse.getEntity();


		// parse xrds
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(entity.getContent());

		Element rootElement = (Element) doc.getDocumentElement();

		rootElement.normalize();

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
