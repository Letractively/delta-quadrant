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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

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

	private Configuration config;
	private Oauth oauth;
	private Logger logger;

	private Services availableServices;
	private ServiceItem selectedService;

	private JTabbedPane tabPane;
	
	private ViewTablelModel tm;
	private JTable table;

	private TrafficTableModel trafficTm;
	private JTable trafficTable;

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

			config = Configuration.parseFile(new File("config.xml"));

			this.fetchServices();

			this.doAuthentication();

			this.add(this.buildMenu(), BorderLayout.NORTH);

			this.add(this.buildBodyPanel(), BorderLayout.CENTER);

			this.add(this.buildTrafficPanel(), BorderLayout.SOUTH);


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
	}
	
	private Component buildMenu()
	{
		JMenuBar menuBar = new JMenuBar();


		// content
		JMenu contentMenu = new JMenu("Content");

		JMenuItem contentGadgetItem = new JMenuItem("Gadget");
		contentGadgetItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/content/gadget"), null);
			}

		});

		JMenuItem contentMediaItem = new JMenuItem("Media");
		contentMediaItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/content/media"), null);
			}

		});

		JMenuItem contentPageItem = new JMenuItem("Page");
		contentPageItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/content/page"), null);
			}

		});

		JMenuItem contentServiceItem = new JMenuItem("Service");
		contentServiceItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/content/service"), null);
			}

		});

		contentMenu.add(contentGadgetItem);
		contentMenu.add(contentMediaItem);
		contentMenu.add(contentPageItem);
		contentMenu.add(contentServiceItem);

		menuBar.add(contentMenu);


		// system
		JMenu systemMenu = new JMenu("System");

		JMenuItem systemApiItem = new JMenuItem("API");
		systemApiItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/system/api"), null);
			}

		});

		JMenuItem systemApprovalItem = new JMenuItem("Approval");
		systemApprovalItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/system/approval"), null);
			}

		});

		JMenuItem systemCountryItem = new JMenuItem("Country");
		systemCountryItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/system/country"), null);
			}

		});

		JMenuItem systemEventItem = new JMenuItem("Event");
		systemEventItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/system/event"), null);
			}

		});

		JMenuItem systemVarsItem = new JMenuItem("Vars");
		systemVarsItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/system/vars"), null);
			}

		});

		systemMenu.add(systemApiItem);
		systemMenu.add(systemApprovalItem);
		systemMenu.add(systemCountryItem);
		systemMenu.add(systemEventItem);
		systemMenu.add(systemVarsItem);

		menuBar.add(systemMenu);


		// user
		JMenu userMenu = new JMenu("User");

		JMenuItem userAccountItem = new JMenuItem("Account");
		userAccountItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/user/account"), null);
			}

		});

		JMenuItem userActivityItem = new JMenuItem("Activity");
		userActivityItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/user/activity"), null);
			}

		});

		JMenuItem userFriendItem = new JMenuItem("Friend");
		userFriendItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/user/friend"), null);
			}

		});

		JMenuItem userGroupItem = new JMenuItem("Friend");
		userGroupItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/user/group"), null);
			}

		});

		JMenuItem userRightItem = new JMenuItem("Right");
		userRightItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/user/right"), null);
			}

		});

		userMenu.add(userAccountItem);
		userMenu.add(userActivityItem);
		userMenu.add(userFriendItem);
		userMenu.add(userGroupItem);
		userMenu.add(userRightItem);

		menuBar.add(userMenu);


		return menuBar;
	}

	private Component buildBodyPanel()
	{
		tabPane = new JTabbedPane();

		tabPane.addTab("View", null);
		tabPane.addTab("Create", null);
		tabPane.addTab("Update", null);
		tabPane.addTab("Delete", null);

		tabPane.setEnabledAt(2, false);
		tabPane.setEnabledAt(3, false);


		tabPane.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				switch(tabPane.getSelectedIndex())
				{
					case 3:

						loadForm(selectedService.getUri() + "/form?method=delete");

						break;

					case 2:

						loadForm(selectedService.getUri() + "/form?method=update");

						break;

					case 1:

						loadForm(selectedService.getUri() + "/form?method=create");

						break;

					default:
					case 0:

						loadService(selectedService, null);

						break;
				}
			}

		});

		return tabPane;
	}

	private Component buildTrafficPanel()
	{
		trafficTable = new JTable(trafficTm);

		trafficTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		trafficTable.getColumnModel().getColumn(0).setMaxWidth(100); 
		trafficTable.getColumnModel().getColumn(1).setMaxWidth(100); 
		trafficTable.getColumnModel().getColumn(2).setMinWidth(600); 

		JScrollPane trafficPane = new JScrollPane(trafficTable);
		trafficPane.setPreferredSize(new Dimension(600, 200));
		
		return trafficPane;
	}

	public void onReady()
	{
		SwingUtilities.invokeLater(new Runnable() {

			public void run()
			{
				loadService(availableServices.getItem("http://ns.amun-project.org/2011/amun/content/page"), null);

				setVisible(true);
			}

		});
	}

	public void loadService(ServiceItem service, ArrayList<String> fields)
	{
		try
		{
			selectedService = service;

			tm = new ViewTablelModel(oauth, service.getUri(), new TrafficListenerInterface() {

				public void handleRequest(TrafficItem item) 
				{
					trafficTm.addTraffic(item);
				}

			});

			if(fields == null)
			{
				tm.loadData(tm.getSupportedFields());
			}
			else
			{
				tm.loadData(fields);
			}

			table = new JTable(tm);


			tabPane.setComponentAt(0, new JScrollPane(table));

			tabPane.validate();
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}
	}

	private void loadForm(String url)
	{
		try
		{
			FormPanel form = new FormPanel(oauth, url, new TrafficListenerInterface() {

				public void handleRequest(TrafficItem item) 
				{
					trafficTm.addTraffic(item);
				}

			});

			form.loadData();

			tabPane.setComponentAt(1, new JScrollPane(form));

			tabPane.validate();
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

	public static boolean hasError(Element element)
	{
		NodeList childs = element.getChildNodes();

		String text = "";
		boolean success = true;

		for(int i = 0; i < childs.getLength(); i++)
		{
			if(childs.item(i).getNodeName().equals("text"))
			{
				text = childs.item(i).getTextContent();
			}

			if(childs.item(i).getNodeName().equals("success"))
			{
				success = !childs.item(i).getTextContent().equals("false");
			}
		}

		if(!success)
		{
			text = text.isEmpty() ? "An unknown error occured" : text;

			JOptionPane.showMessageDialog(null, text);

			return true;
		}
		else
		{
			return false;
		}
	}
}
