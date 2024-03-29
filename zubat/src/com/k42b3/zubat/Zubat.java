/**
 * $Id$
 * 
 * zubat
 * An java application to access the API of amun. It is used to debug and
 * control a website based on amun. This is the reference implementation 
 * howto access the api. So feel free to hack and extend.
 * 
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of zubat. zubat is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * zubat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with zubat. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.zubat;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.ServiceItem;
import com.k42b3.neodym.Services;
import com.k42b3.neodym.TrafficItem;
import com.k42b3.neodym.TrafficListenerInterface;
import com.k42b3.neodym.oauth.Oauth;
import com.k42b3.neodym.oauth.OauthProvider;

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
	private static final long serialVersionUID = 1L;

	public static String version = "0.0.9 beta";

	private static Http http;
	private static Account account;
	private static Services availableServices;
	
	private MenuPanel menuPanel;
	private TreePanel treePanel;
	private ContainerPanel containerPanel;
	private TrafficPanel trafficPanel;

	private TrafficTableModel trafficTm;

	private Logger logger = Logger.getLogger("com.k42b3.zubat");

	public Zubat()
	{
		this.setTitle("zubat (version: " + Zubat.version + ")");
		this.setLocation(100, 100);
		this.setSize(820, 600);
		this.setMinimumSize(this.getSize());
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try
		{
			trafficTm = new TrafficTableModel();

			// http
			http = new Http(new TrafficListenerInterface(){

				public void handleRequest(TrafficItem item)
				{
					trafficTm.addTraffic(item);
				}

			});

			// do authentication
			this.doAuthentication();

			this.fetchAccount();


			menuPanel = new MenuPanel(this);

			this.add(menuPanel, BorderLayout.NORTH);


			treePanel = new TreePanel(this);
			treePanel.setPreferredSize(new Dimension(150, 100));
			treePanel.setMinimumSize(new Dimension(100, 100));
			treePanel.setBorder(new EmptyBorder(4, 0, 4, 4));

			//this.add(treePanel, BorderLayout.WEST);


			containerPanel = new ContainerPanel();
			containerPanel.setMinimumSize(new Dimension(400, 100));
			containerPanel.setBorder(new EmptyBorder(4, 4, 4, 0));

			//this.add(containerPanel, BorderLayout.CENTER);


			JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, containerPanel);
			sp.setBorder(BorderFactory.createEmptyBorder());
			
			this.add(sp, BorderLayout.CENTER);


			trafficPanel = new TrafficPanel(trafficTm);

			trafficPanel.setPreferredSize(new Dimension(600, 200));

			trafficPanel.setBorder(new EmptyBorder(6, 0, 0, 0));

			this.add(trafficPanel, BorderLayout.SOUTH);


			if(http.getOauth().isAuthed())
			{
				onReady();
			}
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}
	}

	private void fetchAccount() throws Exception
	{
		ServiceItem item = getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/service/my/verifyCredentials");

		if(item != null)
		{
			account = Account.buildAccount(http.requestXml(Http.GET, item.getUri()));
		}
		else
		{
			throw new Exception("Could not discover user informations");
		}
	}

	private void doAuthentication() throws Exception
	{
		// fetch services
		availableServices = new Services(http, Configuration.getInstance().getBaseUrl());
		availableServices.discover();

		// authentication
		String requestUrl = availableServices.getItem("http://oauth.net/core/1.0/endpoint/request").getUri();
		String authorizationUrl = availableServices.getItem("http://oauth.net/core/1.0/endpoint/authorize").getUri();
		String accessUrl = availableServices.getItem("http://oauth.net/core/1.0/endpoint/access").getUri();

		OauthProvider provider = new OauthProvider(requestUrl, authorizationUrl, accessUrl, Configuration.getInstance().getConsumerKey(), Configuration.getInstance().getConsumerSecret());
		Oauth oauth = new Oauth(http, provider);

		if(!Configuration.getInstance().getToken().isEmpty() && !Configuration.getInstance().getTokenSecret().isEmpty())
		{
			oauth.auth(Configuration.getInstance().getToken(), Configuration.getInstance().getTokenSecret());
		}
		else
		{
			throw new Exception("No token set use --auth to obtain a token and token secret");
		}

		http.setOauth(oauth);
	}

	public void onReady() throws Exception
	{
		ServiceItem item = availableServices.getItem("http://ns.amun-project.org/2011/amun/content/page");

		if(item != null)
		{
			loadContainer(item);

			setVisible(true);
		}
		else
		{
			throw new Exception("Could not find page service");
		}
	}

	public Component loadContainer(ServiceItem item)
	{
		try
		{
			// load default fields
			ArrayList<String> types = item.getTypes();
			ArrayList<String> fields = new ArrayList<String>();

			for(int i = 0; i < types.size(); i++)
			{
				if(Configuration.getInstance().getServices().containsKey(types.get(i)))
				{
					ArrayList<String> selectedFields = Configuration.getInstance().getServices().get(types.get(i));

					if(selectedFields.size() > 0)
					{
						fields = selectedFields;
					}
				}
			}


			// load container
			Container instance;
			String className = getClassNameFromType(item.getTypeStartsWith("http://ns.amun-project.org/2011/amun"));

			try
			{
				Class<?> container = Class.forName(className);

				instance = (Container) container.newInstance();
			}
			catch(ClassNotFoundException e)
			{
				instance = new com.k42b3.zubat.basic.Container();
			}

			logger.info("Load class " + className);


			// call onload
			instance.onLoad(item, fields);


			// add component
			containerPanel.add(instance.getComponent(), className);

			CardLayout cl = (CardLayout) containerPanel.getLayout();

			cl.show(containerPanel, className);


			return instance.getComponent();
		}
		catch(Exception e)
		{
			Zubat.handleException(e);

			return null;
		}
	}

	public static Http getHttp()
	{
		return http;
	}

	public static Account getAccount()
	{
		return account;
	}

	public static Services getAvailableServices()
	{
		return availableServices;
	}

	public static void handleException(Exception e)
	{
		e.printStackTrace();

		Logger.getLogger("com.k42b3.zubat").warning(e.getMessage());
	}

	public static String getClassNameFromType(String type) throws Exception
	{
		String baseNs = "http://ns.amun-project.org/2011/amun/";

		if(!type.startsWith(baseNs))
		{
			throw new Exception("Type must be in amun namespace");
		}

		type = type.substring(baseNs.length());


		String[] parts = type.split("/");
		String className = "";

		for(int i = 0; i < parts.length; i++)
		{
			className+= parts[i] + ".";
		}

		if(className.isEmpty())
		{
			throw new Exception("Invalid type");
		}

		className = "com.k42b3.zubat.amun." + className + "Container";

		return className;
	}
}
