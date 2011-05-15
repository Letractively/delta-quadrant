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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.k42b3.zubat.form.FormElementInterface;
import com.k42b3.zubat.form.Input;
import com.k42b3.zubat.form.Select;
import com.k42b3.zubat.form.SelectItem;
import com.k42b3.zubat.form.Textarea;

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
	private String url;
	private Http http;
	private Logger logger;

	private String requestMethod;
	private String requestUrl;
	private LinkedHashMap<String, FormElementInterface> requestFields = new LinkedHashMap<String, FormElementInterface>();

	private Container body;
	private JButton btnSend;

	public FormPanel(String url, Http http) throws Exception
	{
		this.url = url;
		this.http = http;
		this.logger = Logger.getLogger("com.k42b3.zubat");


		this.setLayout(new BorderLayout());


		// form panel
		//JPanel panel = new JPanel();
		//panel.setLayout(new FlowLayout(FlowLayout.LEADING));

		body = new JPanel();
		body.setLayout(new GridLayout(0, 1));

		//panel.add(body);

		// load data
		this.request(url);

		this.add(new JScrollPane(body), BorderLayout.CENTER);


		// buttons
		JPanel buttons = new JPanel();

		this.btnSend = new JButton("Send");

		this.btnSend.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					sendRequest();
				}
				catch(Exception ex)
				{
					Zubat.handleException(ex);
				}
			}

		});

		buttons.setLayout(new FlowLayout(FlowLayout.LEADING));

		buttons.add(this.btnSend);

		this.add(buttons, BorderLayout.SOUTH);
	}

	private void request(String url) throws Exception
	{
		HashMap<String, String> header = new HashMap<String, String>();

		header.put("Accept", "application/xml");


		// request
		Document doc = http.requestXml(Http.GET, url, header);


		// parse response
		try
		{
			Element rootElement = (Element) doc.getDocumentElement();

			rootElement.normalize();


			// get message
			Message msg = Http.parseResponse(rootElement);

			if(!msg.hasSuccess())
			{
				JPanel errorPanel = new JPanel();

				errorPanel.setLayout(new FlowLayout());

				errorPanel.add(new JLabel(msg.getText()));

				body.add(errorPanel);
			}
			else
			{
				body.add(this.parse(rootElement));
			}
		}
		catch(SAXException e)
		{
			JLabel error = new JLabel(e.getMessage());

			body.add(error);
		}
	}

	private void sendRequest() throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		doc.setXmlStandalone(true);

		Set<String> keys = requestFields.keySet();

		Element root = doc.createElement("request");

		for(String key : keys)
		{
			Element e = doc.createElement(key);
			e.setTextContent(requestFields.get(key).getValue());

			root.appendChild(e);
		}

		doc.appendChild(root);


		// xml to string
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);

		String requestContent = result.getWriter().toString();


		// send request
		HashMap<String, String> header = new HashMap<String, String>();

		header.put("Accept", "application/xml");
		header.put("Content-type", "application/xml");
		header.put("X-HTTP-Method-Override", requestMethod);


		http.requestXml(Http.POST, requestUrl, header, requestContent);
	}

	private Container parse(Node node) throws Exception
	{
		if(node.getNodeType() == Node.ELEMENT_NODE)
		{
			Node nodeClass = this.getChildNode(node, "class");
			String nodeName = nodeClass.getTextContent().toLowerCase();

			if(nodeName.equals("form"))
			{
				return parseForm(node);
			}
			else if(nodeName.equals("input"))
			{
				return parseInput(node);
			}
			else if(nodeName.equals("textarea"))
			{
				return parseInput(node);
			}
			else if(nodeName.equals("select"))
			{
				return parseSelect(node);
			}
			else if(nodeName.equals("tabbedpane"))
			{
				return parseTabbedPane(node);
			}
			else if(nodeName.equals("panel"))
			{
				return parsePanel(node);
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	private Container parseForm(Node node) throws Exception
	{
		Node nodeMethod = this.getChildNode(node, "method");
		Node nodeAction = this.getChildNode(node, "action");
		Node nodeItem = this.getChildNode(node, "item");

		if(nodeMethod != null)
		{
			requestMethod = nodeMethod.getTextContent();
		}
		else
		{
			throw new Exception("Request method is missing");
		}
		
		if(nodeAction != null)
		{
			requestUrl = nodeAction.getTextContent();
		}
		else
		{
			throw new Exception("Request value is missing");
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		Container con = this.parse(nodeItem); 

		if(con != null)
		{
			panel.add(con, BorderLayout.CENTER);
		}

		return panel;
	}

	private Container parseInput(Node node)
	{
		Node nodeRef = this.getChildNode(node, "ref");
		Node nodeLabel = this.getChildNode(node, "label");
		Node nodeValue = this.getChildNode(node, "value");
		Node nodeDisabled = this.getChildNode(node, "disabled");
		Node nodeType = this.getChildNode(node, "type");

		JPanel item = new JPanel();
		item.setLayout(new FlowLayout());

		JLabel label = new JLabel(nodeLabel.getTextContent());
		label.setPreferredSize(new Dimension(100, 22));

		Input input = new Input();
		input.setPreferredSize(new Dimension(300, 22));
		
		if(nodeValue != null)
		{
			input.setText(nodeValue.getTextContent());
		}

		if(nodeDisabled != null && nodeDisabled.getTextContent().equals("true"))
		{
			input.setEnabled(false);
		}

		item.add(label);
		item.add(input);


		requestFields.put(nodeRef.getTextContent(), input);

		if(nodeType != null && nodeType.getTextContent().equals("hidden"))
		{
			return null;
		}
		else
		{
			return item;
		}
	}

	private Container parseSelect(Node node)
	{
		Node nodeRef = this.getChildNode(node, "ref");
		Node nodeLabel = this.getChildNode(node, "label");
		Node nodeValue = this.getChildNode(node, "value");
		Node nodeDisabled = this.getChildNode(node, "disabled");
		Node nodeChildren = this.getChildNode(node, "children");
		
		JPanel item = new JPanel();
		item.setLayout(new FlowLayout());

		JLabel label = new JLabel(nodeLabel.getTextContent());
		label.setPreferredSize(new Dimension(100, 22));

		if(nodeChildren != null)
		{
			SelectItem[] items = this.getSelectOptions(nodeChildren);
			
			if(items != null)
			{
				DefaultComboBoxModel model = new DefaultComboBoxModel(items);

				Select input = new Select(model);
				input.setPreferredSize(new Dimension(300, 22));

				// set select if value available
				if(nodeValue != null)
				{
					for(int i = 0; i < model.getSize(); i++)
					{
						SelectItem boxItem = (SelectItem) model.getElementAt(i);

						if(boxItem.getKey().equals(nodeValue.getTextContent()))
						{
							input.setSelectedIndex(i);
						}
					}
				}


				if(nodeDisabled != null && nodeDisabled.getTextContent().equals("true"))
				{
					input.setEnabled(false);
				}

				item.add(label);
				item.add(input);


				requestFields.put(nodeRef.getTextContent(), input);

				return item;
			}
		}

		return null;
	}

	private SelectItem[] getSelectOptions(Node node)
	{
		ArrayList<Node> options = this.getChildNodes(node, "item");
		SelectItem[] items = new SelectItem[options.size()];

		for(int i = 0; i < options.size(); i++)
		{
			Node nodeLabel = this.getChildNode(options.get(i), "label");
			Node nodeValue = this.getChildNode(options.get(i), "value");

			if(nodeLabel != null && nodeValue != null)
			{
				items[i] = new SelectItem(nodeValue.getTextContent(), nodeLabel.getTextContent());
			}
		}

		return items;
	}

	private Container parseTextarea(Node node)
	{
		Node nodeRef = this.getChildNode(node, "ref");
		Node nodeLabel = this.getChildNode(node, "label");
		Node nodeValue = this.getChildNode(node, "value");

		JPanel item = new JPanel();
		item.setLayout(new FlowLayout());

		JLabel label = new JLabel(nodeLabel.getTextContent());
		label.setPreferredSize(new Dimension(100, 22));

		Textarea input = new Textarea();
		input.setPreferredSize(new Dimension(300, 120));
		input.setText(nodeValue.getTextContent());

		item.add(label);
		item.add(input);


		requestFields.put(nodeRef.getTextContent(), input);

		return item;
	}

	private Container parseTabbedPane(Node node) throws Exception
	{
		JTabbedPane item = new JTabbedPane();
		Node nodeChildren = this.getChildNode(node, "children");

		if(nodeChildren != null)
		{
			ArrayList<Node> items = this.getChildNodes(nodeChildren, "item");

			for(int i = 0; i < items.size(); i++)
			{
				Node nodeLabel = this.getChildNode(items.get(i), "label");

				if(nodeLabel != null)
				{
					Container con = this.parse(items.get(i)); 

					if(con != null)
					{
						item.addTab(nodeLabel.getTextContent(), con);
					}
				}
			}
		}

		return item;
	}

	private Container parsePanel(Node node) throws Exception
	{
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEADING));

		JPanel item = new JPanel();
		item.setLayout(new GridLayout(0, 1));

		Node nodeChildren = this.getChildNode(node, "children");

		if(nodeChildren != null)
		{
			ArrayList<Node> items = this.getChildNodes(nodeChildren, "item");

			for(int i = 0; i < items.size(); i++)
			{
				Container con = this.parse(items.get(i)); 

				if(con != null)
				{
					item.add(con);
				}
			}
		}

		panel.add(item);

		return panel;
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
