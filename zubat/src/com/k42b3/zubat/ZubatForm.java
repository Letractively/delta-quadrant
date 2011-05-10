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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
 * Form
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 83 $
 */
public class ZubatForm extends JPanel
{
	private String url;
	private Logger logger;

	private String requestMethod;
	private String requestUrl;
	private ArrayList<String> requestFields = new ArrayList<String>();

	public ZubatForm(String url) throws Exception
	{
		this.url = url;
		this.logger = Logger.getLogger("com.k42b3.zubat");


		this.setLayout(new GridLayout());


		// build request
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

		HttpGet getRequest = new HttpGet(url);

		getRequest.addHeader("Accept", "application/xml");

		Zubat.getOauth().signRequest(getRequest);

		logger.info("Request: " + getRequest.getRequestLine());

		HttpResponse httpResponse = httpClient.execute(getRequest);

		HttpEntity entity = httpResponse.getEntity();


		// parse response
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(entity.getContent());

		Element rootElement = (Element) doc.getDocumentElement();

		rootElement.normalize();


		FormItem item = this.parseItemElement(rootElement);

		requestMethod = item.getMethod();
		requestUrl = item.getValue();

		this.parseForm(item);
	}

	private void parseForm(FormItem item)
	{
		for(int i = 0; i < item.getItems().size(); i++)
		{
			if(item.getClassType().equals("input"))
			{
				JPanel panelItem = new JPanel();

				panelItem.setLayout(new FlowLayout());

				JLabel label = new JLabel(item.getLabel());
				label.setPreferredSize(new Dimension(100, 40));
				JTextField input = new JTextField();
				input.setPreferredSize(new Dimension(300, 35));

				panelItem.add(label);
				panelItem.add(input);

				this.add(panelItem);
			}
		}
	}

	private FormItem parseItemElement(Node node)
	{
		FormItem item = new FormItem();
		NodeList childList = node.getChildNodes();

		for(int j = 0; j < childList.getLength(); j++)
		{
			Element childElement = (Element) childList.item(j);

			if(childElement.getLocalName().equals("class"))
			{
				item.setClassType(childElement.getTextContent());
			}

			if(childElement.getLocalName().equals("ref"))
			{
				item.setRef(childElement.getTextContent());
			}

			if(childElement.getLocalName().equals("label"))
			{
				item.setLabel(childElement.getTextContent());
			}

			if(childElement.getLocalName().equals("type"))
			{
				item.setType(childElement.getTextContent());
			}

			if(childElement.getLocalName().equals("value"))
			{
				item.setValue(childElement.getTextContent());
			}

			if(childElement.getLocalName().equals("method"))
			{
				item.setMethod(childElement.getTextContent());
			}

			if(childElement.getLocalName().equals("items"))
			{
				ArrayList<FormItem> items = new ArrayList<FormItem>();
				NodeList childNodes = childElement.getChildNodes();

				for(int i = 0; i < childNodes.getLength(); i++)
				{
					items.add(this.parseItemElement(childNodes.item(i)));
				}

				item.setItems(items);
			}
		}

		return item;
	}
}
