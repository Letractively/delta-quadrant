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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * ViewPanel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class ViewPanel extends JPanel
{
	private Http http;
	private ServiceItem service;
	private ArrayList<String> fields;

	private ViewTableModel tm;
	private JTable table;

	private JPanel search;
	private JPanel buttons;
	private JLabel lblPagination;

	private JTextField txtSearch;
	private JComboBox cboOperator;
	private JComboBox cboField;

	public ViewPanel(Http http, ServiceItem service, ArrayList<String> fields) throws Exception
	{
		this.http = http;
		this.service = service;
		this.fields = fields;


		this.setLayout(new BorderLayout());


		// table
		tm = new ViewTableModel(service.getUri(), http);

		if(fields == null)
		{
			tm.loadData(tm.getSupportedFields());
		}
		else
		{
			tm.loadData(fields);
		}

		table = new JTable(tm);

		this.add(new JScrollPane(table), BorderLayout.CENTER);


		// search bar
		JPanel search = new SearchPanel();

		this.add(search, BorderLayout.NORTH);


		// pagination
		buttons = new ButtonsPanel();

		tm.addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) 
			{
				lblPagination.setText(tm.getStartIndex() + " / " + tm.getItemsPerPage() + " of " + tm.getTotalResults());

				buttons.validate();
			}

		});

		this.add(buttons, BorderLayout.SOUTH);
	}

	public JTable getTable()
	{
		return table;
	}
	
	class SearchPanel extends JPanel
	{
		public SearchPanel()
		{
			JLabel lblSearch = new JLabel("Search:");
			lblSearch.setBorder(new EmptyBorder(5, 5, 5, 5));

			cboField = new JComboBox(new DefaultComboBoxModel(tm.getSupportedFields().toArray()));
			cboField.setPreferredSize(new Dimension(100, 22));

			String[] operators = {"contains", "equals", "startsWith", "present"};
			cboOperator = new JComboBox(new DefaultComboBoxModel(operators));
			cboOperator.setPreferredSize(new Dimension(75, 22));

			txtSearch = new JTextField();
			txtSearch.setPreferredSize(new Dimension(225, 22));
			txtSearch.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) 
				{
					try
					{
						String operator = cboOperator.getSelectedItem().toString();
						String selectedField = cboField.getSelectedItem().toString();
						String value = URLEncoder.encode(txtSearch.getText(), "UTF-8");

						String urlFilter = Http.appendQuery(tm.getBaseUrl(), "filterBy=" + selectedField + "&filterOp=" + operator + "&filterValue=" + value);

						tm.setUrl(urlFilter);
						tm.loadData(tm.getFields());
					}
					catch(Exception ex)
					{
						Zubat.handleException(ex);
					}
				}

			});

			FlowLayout layout = new FlowLayout();
			layout.setAlignment(FlowLayout.LEFT);

			this.setLayout(layout);

			this.add(lblSearch);
			this.add(cboField);
			this.add(cboOperator);
			this.add(txtSearch);
		}
	}

	class ButtonsPanel extends JPanel
	{
		public ButtonsPanel()
		{
			lblPagination = new JLabel(tm.getStartIndex() + " / " + tm.getItemsPerPage() + " of " + tm.getTotalResults());

			JButton btnPrev = new JButton("Prev");
			JButton btnNext = new JButton("Next");

			btnPrev.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) 
				{
					try
					{
						tm.prevPage();
					}
					catch(Exception e)
					{
						Zubat.handleException(e);
					}
				}

			});

			btnNext.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) 
				{
					try
					{
						tm.nextPage();
					}
					catch(Exception e)
					{
						Zubat.handleException(e);
					}
				}

			});

			this.setLayout(new FlowLayout(FlowLayout.CENTER));

			this.add(btnPrev);
			this.add(lblPagination);
			this.add(btnNext);
		}
	}
}
