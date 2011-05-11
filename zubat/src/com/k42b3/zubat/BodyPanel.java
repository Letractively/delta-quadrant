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

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * BodyPanel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class BodyPanel extends JTabbedPane
{
	private Zubat zubat;

	private int selectedId = 0;

	public BodyPanel(Zubat zubatInstance)
	{
		this.zubat = zubatInstance;

		this.addTab("View", null);
		this.addTab("Create", null);
		this.addTab("Update", null);
		this.addTab("Delete", null);

		this.setEnabledAt(2, false);
		this.setEnabledAt(3, false);

		this.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				switch(getSelectedIndex())
				{
					case 3:

						zubat.loadForm(zubat.getSelectedService().getUri() + "/form?method=delete&id=" + selectedId);

						break;

					case 2:

						zubat.loadForm(zubat.getSelectedService().getUri() + "/form?method=update&id=" + selectedId);

						break;

					case 1:

						zubat.loadForm(zubat.getSelectedService().getUri() + "/form?method=create");

						break;

					default:
					case 0:

						zubat.loadService(zubat.getSelectedService(), zubat.getSelectedFields());

						break;
				}
			}

		});

		this.addContainerListener(new ContainerListener() {

			private JTable table;
			private ViewTableModel tm;

			public void componentRemoved(ContainerEvent e) 
			{
				selectedId = 0;
			}

			public void componentAdded(ContainerEvent e) 
			{
				if(e.getComponent() instanceof ViewPanel)
				{
					ViewPanel panel = (ViewPanel) e.getComponent();

					table = panel.getTable();
					tm = (ViewTableModel) table.getModel();

					table.addMouseListener(new MouseListener() {

						public void mouseReleased(MouseEvent e)
						{
							Object rawId = tm.getValueAt(table.getSelectedRow(), 0);

							selectedId = Integer.parseInt(rawId.toString());

							setEnabledAt(2, true);
							setEnabledAt(3, true);
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

		});
	}

	public int getSelectedId()
	{
		return selectedId;
	}
}
