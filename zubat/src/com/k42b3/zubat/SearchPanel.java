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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * SearchPanel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class SearchPanel extends JFrame
{
	private Http http;
	private ReferenceItem item;
	private Logger logger;

	private JTextField txtSearch;
	private JComboBox cboOperator;
	private JComboBox cboField;

	private JTable table;
	private ViewTableModel tm;

	private JButton btnSearch;
	private JButton btnCancel;

	public SearchPanel(Http http, ReferenceItem item) throws Exception
	{
		this.http = http;
		this.item = item;
		this.logger = Logger.getLogger("com.k42b3.zubat");

		this.setTitle("zubat (version: " + Zubat.version + ")");

		this.setLocation(100, 100);

		this.setSize(430, 400);

		this.setMinimumSize(this.getSize());

		this.setLayout(new BorderLayout());


		tm = new ViewTableModel(item.getSrc(), http);


		this.add(this.buildSearch(), BorderLayout.NORTH);

		this.add(this.buildTable(), BorderLayout.CENTER);

		this.add(this.buildButtons(), BorderLayout.SOUTH);
	}
	
	private Component buildSearch()
	{
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new FlowLayout());

		cboField = new JComboBox(new DefaultComboBoxModel(tm.getSupportedFields().toArray()));
		cboField.setPreferredSize(new Dimension(125, 22));

		String[] operators = {"contains", "equals", "startsWith", "present"};
		cboOperator = new JComboBox(new DefaultComboBoxModel(operators));
		cboOperator.setPreferredSize(new Dimension(75, 22));

		txtSearch = new JTextField();
		txtSearch.setPreferredSize(new Dimension(200, 22));

		searchPanel.add(cboField);
		searchPanel.add(cboOperator);
		searchPanel.add(txtSearch);

		return searchPanel;
	}

	private Component buildTable() throws Exception
	{
		ArrayList<String> fields = new ArrayList<String>();

		fields.add(item.getValueField());
		fields.add(item.getLabelField());

		String urlFilter = Http.appendQuery(item.getSrc(), "count=64");

		tm.setUrl(urlFilter);
		tm.loadData(fields);

		table = new JTable(tm);

		table.addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent e) 
			{
				if(e.getClickCount() == 2)
				{
					Object id = tm.getValueAt(table.getSelectedRow(), 0);

					if(id != null)
					{
						item.getInput().setText(id.toString());

						setVisible(false);
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

		return new JScrollPane(table);
	}

	private Component buildButtons()
	{
		JPanel buttons = new JPanel();

		this.btnSearch = new JButton("Search");
		this.btnCancel = new JButton("Cancel");

		this.btnSearch.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					ArrayList<String> fields = new ArrayList<String>();
					String operator = cboOperator.getSelectedItem().toString();
					String selectedField = cboField.getSelectedItem().toString();
					String value = URLEncoder.encode(txtSearch.getText(), "UTF-8");

					fields.add(item.getValueField());
					fields.add(item.getLabelField());

					String urlFilter = Http.appendQuery(item.getSrc(), "filterBy=" + selectedField + "&filterOp=" + operator + "&filterValue=" + value + "&count=64");

					tm.setUrl(urlFilter);
					tm.loadData(fields);
				}
				catch(Exception ex)
				{
					Zubat.handleException(ex);
				}
			}

		});

		this.btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				setVisible(false);
			}

		});

		buttons.setLayout(new FlowLayout(FlowLayout.LEADING));

		buttons.add(this.btnSearch);
		buttons.add(this.btnCancel);
		
		return buttons;
	}
}
