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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
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

	private Container body;
	private JButton btnSend;

	public ZubatForm(String url) throws Exception
	{
		this.url = url;
		this.logger = Logger.getLogger("com.k42b3.zubat");


		this.setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEADING));

		body = new JPanel();
		body.setLayout(new GridLayout(0, 1));

		panel.add(body);

		this.add(panel, BorderLayout.CENTER);


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

		if(Zubat.hasError(rootElement))
		{
			throw new Exception("API error occured");
		}


		this.parseForm(body, this.getChildNodes(rootElement, "items"));


		// buttons
		JPanel buttons = new JPanel();

		this.btnSend = new JButton("Send");

		buttons.setLayout(new FlowLayout(FlowLayout.LEADING));

		buttons.add(this.btnSend);

		this.add(buttons, BorderLayout.SOUTH);
	}

	private void parseForm(Container container, ArrayList<Node> items)
	{
		for(int i = 0; i < items.size(); i++)
		{
			Node childNode = items.get(i);

			if(childNode.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}

			Node nodeClass = this.getChildNode(childNode, "class");
			Node nodeRef = this.getChildNode(childNode, "ref");
			Node nodeLabel = this.getChildNode(childNode, "label");
			Node nodeValue = this.getChildNode(childNode, "value");

			if(nodeClass.getTextContent().equals("input"))
			{
				JPanel item = new JPanel();
				item.setLayout(new FlowLayout());

				JLabel label = new JLabel(nodeLabel.getTextContent());
				label.setPreferredSize(new Dimension(100, 22));
				JTextField input = new JTextField();
				input.setPreferredSize(new Dimension(300, 22));

				item.add(label);
				item.add(input);

				container.add(item);
			}
		}
	}
	
	private ArrayList<Node> getChildNodes(Node node, String nodeName)
	{
		ArrayList<Node> nodes = new ArrayList<Node>();

		NodeList childList = node.getChildNodes();

		for(int i = 0; i < childList.getLength(); i++)
		{
			Node childNode = childList.item(i);

			if(childNode.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}

			if(childNode.getNodeName().equals(nodeName))
			{
				nodes.add(childNode);
			}
		}

		return nodes;
	}
	
	private Node getChildNode(Node node, String nodeName)
	{
		NodeList childList = node.getChildNodes();

		for(int i = 0; i < childList.getLength(); i++)
		{
			Node childNode = childList.item(i);

			if(childNode.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}

			if(childNode.getNodeName().equals(nodeName))
			{
				return childNode;
			}
		}

		return null;
	}
}
