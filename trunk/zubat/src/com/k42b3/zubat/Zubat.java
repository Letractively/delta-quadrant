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
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
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

	private static Oauth oauth;

	private String baseUrl;
	private Logger logger;

	private ZubatListModel lm;
	private JList list;
	
	private ZubatTablelModel tm;
	private JTable table;

	private JTabbedPane tabPane;

	private ServiceItem selectedService;

	public Zubat()
	{
		logger = Logger.getLogger("com.k42b3.zubat");
		
		this.setTitle("zubat (version: " + Zubat.version + ")");

		this.setLocation(100, 100);

		this.setSize(600, 400);

		this.setMinimumSize(this.getSize());

		this.setLayout(new BorderLayout());

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		// create tab
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

						loadTable(selectedService.getUri());

						break;
				}
			}

		});

		this.add(tabPane, BorderLayout.CENTER);


		// @todo this should com frome an config file
		baseUrl = "http://127.0.0.1/projects/amun/public/index.php/";

		String consumerKey = "b8858501073e5fb54e75b973ed044ec19f21a60d";
		String consumerSecret = "07d8b5173afba2575e57ca5966624a39419b5b70";
		String token = "cec314dc235df67c67b5c6b389b12102817135b2";
		String tokenSecret = "5f9b76722e47b03bee7a91afb4946320b3ed4a28";

		String request = "http://127.0.0.1/projects/amun/public/index.php/api/auth/request";
		String authorization = "http://127.0.0.1/projects/amun/public/index.php/api/auth/authorization";
		String access = "http://127.0.0.1/projects/amun/public/index.php/api/auth/access";


		// create provider
		OauthProvider provider = new OauthProvider(request, authorization, access, consumerKey, consumerSecret);
		oauth = new Oauth(provider);

		if(!token.isEmpty() && !tokenSecret.isEmpty())
		{
			oauth.setToken(token);
			oauth.setTokenSecret(tokenSecret);

			loadServices();
			
			onReady();
		}
		else
		{
			new Login().setCallback(new CallbackInterface() {

				public void call()
				{
					loadServices();
					
					onReady();
				}

			});
		}
	}

	public void onReady()
	{
		SwingUtilities.invokeLater(new Runnable() {

			public void run()
			{
				setVisible(true);
			}

		});
	}

	public void loadTable(String uri)
	{
		try
		{
			tm = new ZubatTablelModel(uri);
			tm.loadData(tm.getSupportedFields());

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
			ZubatForm form = new ZubatForm(url);

			tabPane.setComponentAt(1, new JScrollPane(form));

			tabPane.validate();
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}
	}

	public void loadServices()
	{
		try
		{
			lm = new ZubatListModel(baseUrl);
			list = new JList(lm);

			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			list.addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) 
				{
					ServiceItem item = (ServiceItem) list.getSelectedValue();

					if(item != null && !e.getValueIsAdjusting())
					{
						selectedService = item;

						loadTable(item.getUri());
					}
				}

			});

			list.setSelectedIndex(0);

			this.add(new JScrollPane(list), BorderLayout.WEST);
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}
	}

	public static Oauth getOauth()
	{
		return oauth;
	}
	
	public static void handleException(Exception e)
	{
		e.printStackTrace();

		Logger.getLogger("com.k42b3.zubat").warning(e.getMessage());
	}
}
