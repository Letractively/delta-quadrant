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
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.k42b3.zubat.oauth.OauthProvider;

/**
 * Zubat
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Zubat extends JFrame
{
	public static String version = "0.0.1 beta";
	public static String lastMessage;

	private Configuration config;
	private Oauth oauth;
	private Logger logger;

	private Services availableServices;
	
	private ServiceItem selectedService;
	private ArrayList<String> selectedFields;

	private MenuPanel menuPanel;
	private BodyPanel bodyPanel;
	private TrafficPanel trafficPanel;

	private TrafficTableModel trafficTm;
	
	public Zubat()
	{
		logger = Logger.getLogger("com.k42b3.zubat");
		
		this.setTitle("zubat (version: " + Zubat.version + ")");

		this.setLocation(100, 100);

		this.setSize(800, 600);

		this.setMinimumSize(this.getSize());

		this.setLayout(new BorderLayout());

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		try
		{
			// model
			trafficTm = new TrafficTableModel();

			config = Configuration.parseFile(new File("zubat.conf.xml"));

			this.fetchServices();

			this.doAuthentication();

			menuPanel = new MenuPanel(this);
			bodyPanel = new BodyPanel(this);
			trafficPanel = new TrafficPanel(trafficTm);
			
			
			this.add(menuPanel, BorderLayout.NORTH);

			this.add(bodyPanel, BorderLayout.CENTER);

			this.add(trafficPanel, BorderLayout.SOUTH);


			if(oauth.isAuthed())
			{
				onReady();
			}
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}
	}

	public Services getAvailableServices()
	{
		return availableServices;
	}

	public ServiceItem getSelectedService()
	{
		return selectedService;
	}

	public ArrayList<String> getSelectedFields()
	{
		return selectedFields;
	}

	private void doAuthentication() throws Exception
	{
		String requestUrl = availableServices.getItem("http://oauth.net/core/1.0/endpoint/request").getUri();
		String authorizationUrl = availableServices.getItem("http://oauth.net/core/1.0/endpoint/authorize").getUri();
		String accessUrl = availableServices.getItem("http://oauth.net/core/1.0/endpoint/access").getUri();

		OauthProvider provider = new OauthProvider(requestUrl, authorizationUrl, accessUrl, config.getConsumerKey(), config.getConsumerSecret());
		oauth = new Oauth(provider, new TrafficListenerInterface() {

			public void handleRequest(TrafficItem item) 
			{
				trafficTm.addTraffic(item);
			}

		});

		if(!config.getToken().isEmpty() && !config.getTokenSecret().isEmpty())
		{
			oauth.auth(config.getToken(), config.getTokenSecret());
		}
		else
		{
			throw new Exception("No token set use --auth to obtain a token and token secret");
		}
	}

	private void fetchServices() throws Exception
	{
		availableServices = new Services(config.getBaseUrl(), new TrafficListenerInterface() {

			public void handleRequest(TrafficItem item) 
			{
				trafficTm.addTraffic(item);
			}

		});
		
		availableServices.loadData();
	}

	public void onReady()
	{
		SwingUtilities.invokeLater(new Runnable() {

			public void run()
			{
				ArrayList<String> fields = new ArrayList<String>();

				fields.add("id");
				fields.add("parentId");
				fields.add("status");
				fields.add("load");
				fields.add("application");
				fields.add("title");
				fields.add("template");
				fields.add("cache");
				fields.add("expire");
				fields.add("date");
				fields.add("url");

				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/content/page"), fields);

				setVisible(true);
			}

		});
	}

	public void loadService(ServiceItem service, ArrayList<String> fields)
	{
		try
		{
			selectedService = service;
			selectedFields = fields;


			ViewPanel view = new ViewPanel(oauth, service, fields, new TrafficListenerInterface() {

				public void handleRequest(TrafficItem item)
				{
					trafficTm.addTraffic(item);
				}

			});


			bodyPanel.setComponentAt(0, view);

			bodyPanel.setSelectedIndex(0);

			bodyPanel.validate();
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}
	}

	public void loadForm(int tabIndex, String url)
	{
		try
		{
			FormPanel form = new FormPanel(oauth, url, new TrafficListenerInterface() {

				public void handleRequest(TrafficItem item) 
				{
					trafficTm.addTraffic(item);
				}

			});


			bodyPanel.setComponentAt(tabIndex, form);

			bodyPanel.validate();
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}
	}
	
	public static void handleException(Exception e)
	{
		e.printStackTrace();

		Logger.getLogger("com.k42b3.zubat").warning(e.getMessage());
	}

	public static Message parseResponse(Element element)
	{
		NodeList childs = element.getChildNodes();
		Message msg = new Message();

		for(int i = 0; i < childs.getLength(); i++)
		{
			if(childs.item(i).getNodeName().equals("text"))
			{
				msg.setText(childs.item(i).getTextContent());
			}

			if(childs.item(i).getNodeName().equals("success"))
			{
				msg.setSuccess(!childs.item(i).getTextContent().equals("false"));
			}
		}


		lastMessage = msg.getText();

		if(!lastMessage.isEmpty())
		{
			SwingUtilities.invokeLater(new Runnable() {

				public void run() 
				{
					JOptionPane.showMessageDialog(null, lastMessage);
				}

			});
		}

		return msg;
	}

	public static String appendQuery(String url, String query)
	{
		if(url.indexOf('?') == -1)
		{
			return url + '?' + query;
		}
		else
		{
			return url + '&' + query;
		}
	}
}
