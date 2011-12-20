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

package com.k42b3.zubat.amun.user.activity;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.ServiceItem;

/**
 * ViewPanel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class ViewPanel extends com.k42b3.zubat.basic.ViewPanel
{
	private static final long serialVersionUID = 1L;

	public ViewPanel(Http http, ServiceItem service, ArrayList<String> fields) throws Exception 
	{
		super(http, service, fields);
	}

	protected ViewTableModel getTableModel() throws Exception
	{
		ViewTableModel tm = new ViewTableModel(service.getUri(), http);

		if(fields == null || fields.size() == 0)
		{
			tm.loadData(tm.getSupportedFields());
		}
		else
		{
			tm.loadData(fields);
		}

		return tm;
	}

	protected Component buildTable()
	{
		table = new ActivityTable(tm);

		return new JScrollPane(table);
	}

	class ActivityTable extends JTable
	{
		private static final long serialVersionUID = 1L;

		protected TableCellRenderer cellRenderer;

		public ActivityTable(com.k42b3.zubat.basic.ViewTableModel tm) 
		{
			super(tm);

			setRowHeight(50);

			cellRenderer = new ActivityRenderer();
		}

		public TableCellRenderer getCellRenderer(int row, int column)
		{
			return cellRenderer;
		}

		class ActivityRenderer implements TableCellRenderer
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
			{
				if(value != null)
				{
					ViewTableModel tm = (ViewTableModel) table.getModel();
					String title = (String) tm.getValueAt(row, 1);
					String date = (String) tm.getValueAt(row, 2);
					String user = (String) tm.getValueAt(row, 3);

					JLabel lblTitle;

					if(hasFocus)
					{
						lblTitle = new JLabel("<html><font size=+1><b>" + title + "</b></font><br><small>created on: " + date + " by " + user + "</small></html>");
					}
					else
					{
						lblTitle = new JLabel("<html><font size=+1>" + title + "</font><br><small>created on: " + date + " by " + user + "</small></html>");
					}

					lblTitle.setBorder(new EmptyBorder(5, 5, 5, 5));

					return lblTitle;
				}
				else
				{
					return null;
				}
			}
		}
	}
}
