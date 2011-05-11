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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * MenuPanel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class MenuPanel extends JMenuBar
{
	private Zubat zubat;

	public MenuPanel(Zubat zubatInstance)
	{
		this.zubat = zubatInstance;
		
		this.buildContentMenu();
		this.buildSystemMenu();
		this.buildUserMenu();
		this.buildHelpMenu();
	}
	
	private void buildContentMenu()
	{
		JMenu menu = new JMenu("Content");

		JMenuItem gadgetItem = new JMenuItem("Gadget");
		gadgetItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				ArrayList<String> fields = new ArrayList<String>();

				fields.add("id");
				fields.add("title");
				fields.add("path");
				fields.add("cache");
				fields.add("expire");
				fields.add("date");
				fields.add("authorName");

				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/content/gadget"), fields);
			}

		});

		JMenuItem mediaItem = new JMenuItem("Media");
		mediaItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				ArrayList<String> fields = new ArrayList<String>();

				fields.add("id");
				fields.add("title");
				fields.add("path");
				fields.add("type");
				fields.add("size");
				fields.add("mimeType");
				fields.add("url");
				fields.add("date");
				fields.add("authorName");

				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/content/media"), fields);
			}

		});

		JMenuItem pageItem = new JMenuItem("Page");
		pageItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
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

				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/content/page"), fields);
			}

		});

		JMenuItem serviceItem = new JMenuItem("Service");
		serviceItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/content/service"), null);
			}

		});

		menu.add(gadgetItem);
		menu.add(mediaItem);
		menu.add(pageItem);
		menu.add(serviceItem);

		this.add(menu);
	}

	private void buildSystemMenu()
	{
		JMenu menu = new JMenu("System");

		JMenuItem apiItem = new JMenuItem("API");
		apiItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/system/api"), null);
			}

		});

		JMenuItem approvalItem = new JMenuItem("Approval");
		approvalItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/system/approval"), null);
			}

		});

		JMenuItem countryItem = new JMenuItem("Country");
		countryItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/system/country"), null);
			}

		});

		JMenuItem eventItem = new JMenuItem("Event");
		eventItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				ArrayList<String> fields = new ArrayList<String>();

				fields.add("id");
				fields.add("priority");
				fields.add("type");
				fields.add("table");
				fields.add("actionName");

				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/system/event"), fields);
			}

		});

		JMenuItem varsItem = new JMenuItem("Vars");
		varsItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/system/vars"), null);
			}

		});

		menu.add(apiItem);
		menu.add(approvalItem);
		menu.add(countryItem);
		menu.add(eventItem);
		menu.add(varsItem);

		this.add(menu);
	}
	
	private void buildUserMenu()
	{
		JMenu menu = new JMenu("User");

		JMenuItem accountItem = new JMenuItem("Account");
		accountItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				ArrayList<String> fields = new ArrayList<String>();

				fields.add("id");
				fields.add("status");
				fields.add("name");
				fields.add("email");
				fields.add("gender");
				fields.add("timezone");
				fields.add("lastSeen");
				fields.add("updated");
				fields.add("date");
				fields.add("profileUrl");
				fields.add("thumbnailUrl");
				fields.add("groupTitle");
				fields.add("countryTitle");

				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/user/account"), fields);
			}

		});

		JMenuItem activityItem = new JMenuItem("Activity");
		activityItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				ArrayList<String> fields = new ArrayList<String>();

				fields.add("id");
				fields.add("title");
				fields.add("url");
				fields.add("body");
				fields.add("date");
				fields.add("authorName");

				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/user/activity"), fields);
			}

		});

		JMenuItem friendItem = new JMenuItem("Friend");
		friendItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				ArrayList<String> fields = new ArrayList<String>();

				fields.add("id");
				fields.add("status");
				fields.add("date");
				fields.add("authorName");
				fields.add("friendName");

				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/user/friend"), fields);
			}

		});

		JMenuItem groupItem = new JMenuItem("Group");
		groupItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/user/group"), null);
			}

		});

		JMenuItem rightItem = new JMenuItem("Right");
		rightItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				zubat.loadService(zubat.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/user/right"), null);
			}

		});

		menu.add(accountItem);
		menu.add(activityItem);
		menu.add(friendItem);
		menu.add(groupItem);
		menu.add(rightItem);

		this.add(menu);
	}
	
	private void buildHelpMenu()
	{
		JMenu menu = new JMenu("Help");

		JMenuItem websiteItem = new JMenuItem("Website");
		websiteItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				String websiteUrl = "http://amun.phpsx.org";

				try
				{
					URI websiteUri = new URI(websiteUrl);

					if(Desktop.isDesktopSupported())
					{
						Desktop desktop = Desktop.getDesktop();

						if(desktop.isSupported(Desktop.Action.BROWSE))
						{
							desktop.browse(websiteUri);
						}
						else
						{
							JOptionPane.showMessageDialog(null, websiteUrl);
						}
					}
					else
					{
						JOptionPane.showMessageDialog(null, websiteUrl);
					}
				}
				catch(Exception ex)
				{
					Zubat.handleException(ex);

					JOptionPane.showMessageDialog(null, websiteUrl);
				}
			}

		});

		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				JOptionPane.showMessageDialog(null, "zubat (version: " + Zubat.version + ")\nDeveloper: Christoph Kappestein <k42b3.x@gmail.com>\nLicense: http://www.gnu.org/licenses/gpl.html GPLv3");
			}

		});

		menu.add(websiteItem);
		menu.add(aboutItem);

		this.add(menu);
	}
}
