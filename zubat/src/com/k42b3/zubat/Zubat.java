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
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

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
	public static String version = "0.0.3 beta";

	private Configuration config;
	private Oauth oauth;
	private Http http;
	private Logger logger;

	private Services availableServices;
	
	private ServiceItem selectedService;
	private ArrayList<String> selectedFields;

	private MenuPanel menuPanel;
	private TreePanel treePanel;
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

			http = new Http(new TrafficListenerInterface(){

				public void handleRequest(TrafficItem item)
				{
					trafficTm.addTraffic(item);
				}

			});

			config = Configuration.parseFile(Configuration.getFile());

			this.fetchServices();

			this.doAuthentication();


			menuPanel = new MenuPanel(this);

			this.add(menuPanel, BorderLayout.NORTH);


			treePanel = new TreePanel(http, availableServices);

			treePanel.setPreferredSize(new Dimension(150, 100));

			treePanel.setBorder(new EmptyBorder(0, 0, 0, 6));

			this.add(treePanel, BorderLayout.WEST);


			bodyPanel = new BodyPanel(this);

			this.add(bodyPanel, BorderLayout.CENTER);


			trafficPanel = new TrafficPanel(trafficTm);

			trafficPanel.setPreferredSize(new Dimension(600, 200));

			trafficPanel.setBorder(new EmptyBorder(6, 0, 0, 0));

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

	public Configuration getConfig()
	{
		return config;
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
		oauth = new Oauth(http, provider);

		if(!config.getToken().isEmpty() && !config.getTokenSecret().isEmpty())
		{
			oauth.auth(config.getToken(), config.getTokenSecret());
		}
		else
		{
			throw new Exception("No token set use --auth to obtain a token and token secret");
		}

		http.setOauth(oauth);
	}

	private void fetchServices() throws Exception
	{
		availableServices = new Services(config.getBaseUrl(), http);
		
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


			ViewPanel view = new ViewPanel(http, service, fields);


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
			FormPanel form = new FormPanel(url, http);


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


}
