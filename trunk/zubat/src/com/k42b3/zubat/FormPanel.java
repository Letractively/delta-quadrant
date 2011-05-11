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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import org.xml.sax.InputSource;

/**
 * Form
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class FormPanel extends JPanel
{
	private Oauth oauth;
	private String url;
	private Logger logger;
	private TrafficListenerInterface trafficListener;

	private String requestMethod;
	private String requestUrl;
	private ArrayList<String> requestFields = new ArrayList<String>();

	private Container body;
	private JButton btnSend;

	public FormPanel(Oauth oauth, String url)
	{
		this.oauth = oauth;
		this.url = url;
		this.logger = Logger.getLogger("com.k42b3.zubat");


		this.setLayout(new BorderLayout());


		// form panel
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEADING));

		body = new JPanel();
		body.setLayout(new GridLayout(0, 1));

		panel.add(body);

		this.add(new JScrollPane(panel), BorderLayout.CENTER);


		// buttons
		JPanel buttons = new JPanel();

		this.btnSend = new JButton("Send");

		buttons.setLayout(new FlowLayout(FlowLayout.LEADING));

		buttons.add(this.btnSend);

		this.add(buttons, BorderLayout.SOUTH);
	}

	public FormPanel(Oauth oauth, String url, TrafficListenerInterface trafficListener)
	{
		this(oauth, url);

		this.trafficListener = trafficListener;
	}

	public void loadData() throws Exception
	{
		this.request(url);
	}

	private void request(String url) throws Exception
	{
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

		HttpGet getRequest = new HttpGet(url);

		getRequest.addHeader("Accept", "application/xml");

		oauth.signRequest(getRequest);

		logger.info("Request: " + getRequest.getRequestLine());

		HttpResponse httpResponse = httpClient.execute(getRequest);

		HttpEntity entity = httpResponse.getEntity();

		String responseContent = Zubat.getEntityContent(entity);


		// log traffic
		if(trafficListener != null)
		{
			TrafficItem trafficItem = new TrafficItem();

			trafficItem.setRequest(getRequest);
			trafficItem.setResponse(httpResponse);
			trafficItem.setResponseContent(responseContent);

			trafficListener.handleRequest(trafficItem);
		}


		// parse response
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(responseContent));

		Document doc = db.parse(is);

		Element rootElement = (Element) doc.getDocumentElement();

		rootElement.normalize();

		if(Zubat.hasError(rootElement))
		{
			body.removeAll();
		}
		else
		{
			this.parseForm(body, this.getChildNodes(rootElement, "items"));
		}
	}

	private void parseForm(Container container, ArrayList<Node> items)
	{
		for(int i = 0; i < items.size(); i++)
		{
			Node node = items.get(i);

			if(node.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}

			Node nodeClass = this.getChildNode(node, "class");

			if(nodeClass.getTextContent().equals("input"))
			{
				container.add(parseInput(node));
			}

			if(nodeClass.getTextContent().equals("select"))
			{
				container.add(parseSelect(node));
			}
		}
	}

	private Component parseInput(Node node)
	{
		Node nodeRef = this.getChildNode(node, "ref");
		Node nodeLabel = this.getChildNode(node, "label");
		Node nodeValue = this.getChildNode(node, "value");

		JPanel item = new JPanel();
		item.setLayout(new FlowLayout());

		JLabel label = new JLabel(nodeLabel.getTextContent());
		label.setPreferredSize(new Dimension(100, 22));
		JTextField input = new JTextField();
		input.setPreferredSize(new Dimension(300, 22));

		item.add(label);
		item.add(input);

		return item;
	}

	private Component parseSelect(Node node)
	{
		Node nodeRef = this.getChildNode(node, "ref");
		Node nodeLabel = this.getChildNode(node, "label");
		Node nodeValue = this.getChildNode(node, "value");

		JPanel item = new JPanel();
		item.setLayout(new FlowLayout());

		JLabel label = new JLabel(nodeLabel.getTextContent());
		label.setPreferredSize(new Dimension(100, 22));


		JComboBox input = new JComboBox(new DefaultComboBoxModel(this.getSelectOptions(node)));
		input.setPreferredSize(new Dimension(300, 22));

		item.add(label);
		item.add(input);

		return item;
	}

	private ComboBoxItem[] getSelectOptions(Node node)
	{
		ArrayList<Node> options = this.getChildNodes(node, "items");
		ComboBoxItem[] items = new ComboBoxItem[options.size()];

		for(int i = 0; i < options.size(); i++)
		{
			Node nodeLabel = this.getChildNode(options.get(i), "label");
			Node nodeValue = this.getChildNode(options.get(i), "value");

			if(nodeLabel != null && nodeValue != null)
			{
				items[i] = new ComboBoxItem(nodeLabel.getTextContent(), nodeValue.getTextContent());
			}
		}

		return items;
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

	class ComboBoxItem
	{
		private String key;
		private String value;

		public ComboBoxItem(String key, String value)
		{
			this.setKey(key);
			this.setValue(value);
		}

		public String getKey() 
		{
			return key;
		}

		public void setKey(String key) 
		{
			this.key = key;
		}

		public String getValue() 
		{
			return value;
		}

		public void setValue(String value) 
		{
			this.value = value;
		}
		
		public String toString()
		{
			return this.value;
		}
	}
}
