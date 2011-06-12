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

		this.add(this.buildMenu("http://ns.amun-project.org/2011/amun/content"));
		this.add(this.buildMenu("http://ns.amun-project.org/2011/amun/system"));
		this.add(this.buildMenu("http://ns.amun-project.org/2011/amun/user"));
		this.add(this.buildMenu("http://ns.amun-project.org/2011/amun/service"));
		this.add(this.buildHelpMenu());
	}

	private JMenu buildMenu(String baseUrl)
	{
		Services services = zubat.getAvailableServices();

		int baseDeep = this.charCount('/', baseUrl) + 1;
		String baseTitle = baseUrl.substring(baseUrl.lastIndexOf("/") + 1);
		baseTitle = (baseTitle.charAt(0) + "").toUpperCase() + baseTitle.substring(1).toLowerCase();

		JMenu menu = new JMenu(baseTitle);

		for(int i = 0; i < services.getSize(); i++)
		{
			ServiceItem item = services.getElementAt(i);
			String type = item.getTypeStartsWith(baseUrl);

			if(type != null)
			{
				int deep = this.charCount('/', type);
				String title = type.substring(type.lastIndexOf("/") + 1);
				title = (title.charAt(0) + "").toUpperCase() + title.substring(1).toLowerCase();

				if(deep == baseDeep)
				{
					JMenu childMenu = this.buildMenu(type);

					if(childMenu.getItemCount() == 0)
					{
						JMenuItem serviceItem = new JMenuItem(title);
						serviceItem.addActionListener(new MenuItemListener(item));

						menu.add(serviceItem);
					}
					else
					{
						JMenuItem serviceItem = new JMenuItem(title);
						serviceItem.addActionListener(new MenuItemListener(item));

						childMenu.insert(serviceItem, 0);

						menu.add(childMenu);
					}
				}
			}
		}

		return menu;
	}

	private JMenu buildHelpMenu()
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

		return menu;
	}

	private int charCount(char c, String content)
	{
		int j = 0;

		for(int i = 0; i < content.length(); i++)
		{
			if(content.charAt(i) == c)
			{
				j++;
			}
		}

		return j;
	}

	class MenuItemListener implements ActionListener
	{
		private ServiceItem item;
		private ArrayList<String> fields;

		public MenuItemListener(ServiceItem item)
		{
			this.item = item;
			this.fields = null;

			ArrayList<String> types = item.getTypes();

			for(int i = 0; i < types.size(); i++)
			{
				if(zubat.getConfig().getServices().containsKey(types.get(i)))
				{
					ArrayList<String> selectedFields = zubat.getConfig().getServices().get(types.get(i));
					
					if(selectedFields.size() > 0)
					{
						this.fields = selectedFields;
					}
				}
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			zubat.loadService(item, fields);
		}
	}
}
