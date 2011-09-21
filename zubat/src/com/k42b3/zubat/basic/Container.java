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

package com.k42b3.zubat.basic;

import java.awt.Component;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.ServiceItem;
import com.k42b3.zubat.Zubat;

/**
 * Container
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Container extends JTabbedPane implements com.k42b3.zubat.Container
{
	public final static int RECEIVE = 0x0;
	public final static int CREATE = 0x1;
	public final static int UPDATE = 0x2;
	public final static int DELETE = 0x3;

	private Http http;
	private ServiceItem item;
	private ArrayList<String> fields;

	private int selectedId = 0;

	public Container()
	{
		this.addTab("View", null);
		this.addTab("Create", null);
		this.addTab("Update", null);
		this.addTab("Delete", null);

		this.setEnabledAt(UPDATE, false);
		this.setEnabledAt(DELETE, false);

		this.addChangeListener(new ContainerChangeListener());
		this.addContainerListener(new ContainerContainerListener());
	}

	public void onLoad(Http http, ServiceItem item, ArrayList<String> fields)
	{
		this.http = http;
		this.item = item;
		this.fields = fields;

		renderTabs();
	}

	public Component getComponent() 
	{
		return this;
	}

	public void renderTabs()
	{
		switch(getSelectedIndex())
		{
			case DELETE:

				try
				{
					if(selectedId <= 0)
					{
						throw new Exception("No row selected");
					}

					FormPanel form = new FormPanel(item.getUri() + "/form?method=delete&id=" + selectedId, http);

					setComponentAt(DELETE, form);

					validate();
				}
				catch(Exception ex)
				{
					Zubat.handleException(ex);
				}

				break;

			case UPDATE:

				try
				{
					if(selectedId <= 0)
					{
						throw new Exception("No row selected");
					}

					FormPanel form = new FormPanel(item.getUri() + "/form?method=update&id=" + selectedId, http);

					setComponentAt(UPDATE, form);

					validate();
				}
				catch(Exception ex)
				{
					Zubat.handleException(ex);
				}

				break;

			case CREATE:

				try
				{
					FormPanel form = new FormPanel(item.getUri() + "/form?method=create", http);

					setComponentAt(CREATE, form);

					validate();
				}
				catch(Exception ex)
				{
					Zubat.handleException(ex);
				}

				break;

			default:
			case RECEIVE:

				try
				{
					selectedId = 0;

					setEnabledAt(2, false);
					setEnabledAt(3, false);

					setSelectedIndex(0);


					ViewPanel view = new ViewPanel(http, item, fields);

					setComponentAt(RECEIVE, view);

					setSelectedIndex(0);

					validate();
				}
				catch(Exception ex)
				{
					Zubat.handleException(ex);
				}

				break;
		}
	}

	class ContainerChangeListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			renderTabs();
		}
	}
	
	class ContainerContainerListener implements ContainerListener
	{
		private JTable table;
		private ViewTableModel tm;

		public void componentRemoved(ContainerEvent e) 
		{
		}

		public void componentAdded(ContainerEvent e) 
		{
			if(e.getComponent() instanceof Container)
			{
				Container panel = (Container) e.getComponent();
				Component viewComponent = panel.getComponentAt(0);

				if(viewComponent instanceof ViewPanel)
				{
					ViewPanel viewPanel = (ViewPanel) viewComponent;

					table = viewPanel.getTable();
					tm = (ViewTableModel) table.getModel();

					table.addMouseListener(new MouseListener() {

						public void mouseReleased(MouseEvent e)
						{
							Object rawId = tm.getValueAt(table.getSelectedRow(), 0);

							if(rawId != null)
							{
								int id = Integer.parseInt(rawId.toString());

								if(id > 0)
								{
									selectedId = id;

									setEnabledAt(2, true);
									setEnabledAt(3, true);
								}
							}
						}

						public void mousePressed(MouseEvent e) 
						{
						}

						public void mouseExited(MouseEvent e) 
						{
						}

						public void mouseEntered(MouseEvent e) 
						{
						}

						public void mouseClicked(MouseEvent e) 
						{
						}

					});
				}
			}
		}
	}
}
