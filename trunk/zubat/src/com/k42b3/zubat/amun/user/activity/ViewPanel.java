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

import java.awt.Component;
import java.awt.SystemColor;
import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

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

	public ViewPanel(ServiceItem service, ArrayList<String> fields) throws Exception 
	{
		super(service, fields);
	}

	protected ViewTableModel getTableModel() throws Exception
	{
		ViewTableModel tm = new ViewTableModel(service.getUri());

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
					String summary = (String) tm.getValueAt(row, 1);
					String date = (String) tm.getValueAt(row, 2);
					String user = (String) tm.getValueAt(row, 3);

					// build editor pane
					String html = "<html><body>" + summary + "<br /><small>created on: " + date + " by " + user + "</small></body></html>";

					JEditorPane pane = new JEditorPane("text/html", html);
					pane.setSize(table.getSize().width, Integer.MAX_VALUE);
					pane.setEditable(false);

					HTMLEditorKit kit = new HTMLEditorKit();
					pane.setEditorKit(kit);

					StyleSheet styleSheet = kit.getStyleSheet();
					styleSheet.addRule("body {font-family:\"Helvetica Neue\", Arial, Helvetica, sans-serif;}");
					styleSheet.addRule("h1 {margin:2px;padding:2px;}");
					styleSheet.addRule("p {margin:2px;padding:2px;}");

					Document doc = kit.createDefaultDocument();
					pane.setDocument(doc);
					pane.setText(html);

					if(hasFocus)
					{
						pane.setOpaque(true);
						pane.setBackground(SystemColor.activeCaption);
						pane.setForeground(SystemColor.activeCaptionText);
					}

					// set height
					if(pane.getPreferredSize().height != table.getRowHeight(row))
					{
						table.setRowHeight(row, pane.getPreferredSize().height); 
					}

					return pane;
				}
				else
				{
					return null;
				}
			}
		}
	}
}
