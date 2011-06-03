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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.k42b3.zubat.oauth.OauthProvider;

/**
 * Login
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Auth extends JFrame
{
	private Configuration config;
	private Services availableServices;
	private Oauth oauth;
	private Http http;
	private Logger logger;

	private TrafficTableModel trafficTm;

	public Auth()
	{
		this.logger = Logger.getLogger("com.k42b3.zubat");

		this.setTitle("zubat (version: " + Zubat.version + ")");

		this.setLocation(100, 100);

		this.setSize(500, 200);

		this.setMinimumSize(this.getSize());

		this.setResizable(false);

		this.setLayout(new BorderLayout());


		try
		{
			// status
			Configuration config = Configuration.parseFile(Configuration.getFile());

			JLabel status;

			if(config.getConsumerKey().trim().isEmpty())
			{
				status = new JLabel("Please provide a consumer key in the configuration.");
			}
			else if(config.getConsumerSecret().trim().isEmpty())
			{
				status = new JLabel("Please provide a consumer secret in the configuration.");
			}
			else
			{
				status = new JLabel("Click on \"Login\" to start the authentication.");
			}

			status.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

			this.add(status, BorderLayout.NORTH);


			// traffic panel
			trafficTm = new TrafficTableModel();

			http = new Http(new TrafficListenerInterface(){

				public void handleRequest(TrafficItem item)
				{
					trafficTm.addTraffic(item);
				}

			});

			TrafficPanel trafficPanel = new TrafficPanel(trafficTm);

			this.add(trafficPanel, BorderLayout.CENTER);


			// oauth config
			availableServices = new Services(config.getBaseUrl(), http);
			availableServices.loadData();

			ServiceItem request = availableServices.getItem("http://oauth.net/core/1.0/endpoint/request");
			ServiceItem authorization = availableServices.getItem("http://oauth.net/core/1.0/endpoint/authorize");
			ServiceItem access = availableServices.getItem("http://oauth.net/core/1.0/endpoint/access");

			if(request == null)
			{
				throw new Exception("Could not find request service");
			}

			if(authorization == null)
			{
				throw new Exception("Could not find authorization service");
			}

			if(access == null)
			{
				throw new Exception("Could not find access service");
			}

			OauthProvider provider = new OauthProvider(request.getUri(), authorization.getUri(), access.getUri(), config.getConsumerKey(), config.getConsumerSecret());
			oauth = new Oauth(http, provider);
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}

		
		// buttons
		JPanel buttons = new JPanel();

		buttons.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		JButton btnLogin = new JButton("Login");
		JButton btnClose = new JButton("Close");

		btnLogin.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				if(oauth.requestToken())
				{
					if(oauth.authorizeToken())
					{
						if(oauth.accessToken())
						{
							JOptionPane.showMessageDialog(null, "You have successful authenticated");

							saveConfig(oauth.getToken(), oauth.getTokenSecret());
						}
						else
						{
							logger.warning("Could not get access token");
						}
					}
					else
					{
						logger.warning("Could not authorize token");
					}
				}
				else
				{
					logger.warning("Could not get request token");
				}
			}

		});

		btnClose.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}

		});

		buttons.add(btnLogin);
		buttons.add(btnClose);

		this.add(buttons, BorderLayout.SOUTH);


		this.setVisible(true);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void saveConfig(String token, String tokenSecret)
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(Configuration.getFile());

			Element rootElement = (Element) doc.getDocumentElement();

			rootElement.normalize();


			// add token / tokenSecret element
			Element tokenElement = (Element) doc.getElementsByTagName("token").item(0);
			Element tokenSecretElement = (Element) doc.getElementsByTagName("tokenSecret").item(0);

			if(tokenElement != null)
			{
				tokenElement.setTextContent(token);
			}
			else
			{
				tokenElement = doc.createElement("token");
				tokenElement.setTextContent(token);

				doc.appendChild(tokenElement);
			}

			if(tokenSecretElement != null)
			{
				tokenSecretElement.setTextContent(tokenSecret);
			}
			else
			{
				tokenSecretElement = doc.createElement("tokenSecret");
				tokenSecretElement.setTextContent(tokenSecret);

				doc.appendChild(tokenSecretElement);
			}


			// save dom
			Source source = new DOMSource(doc);

			Result result = new StreamResult(Configuration.getFile());

			Transformer xformer = TransformerFactory.newInstance().newTransformer();

			xformer.transform(source, result);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "The following error occured: " + e.getMessage() + "\nNevertheless you can use the obtained token and token secret by adding them manually to the configuration file.\n\nToken: " + token + "\nToken secret: " + tokenSecret);

			Zubat.handleException(e);
		}
	}
}
