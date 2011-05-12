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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
	private Oauth oauth;
	private ServiceItem service;
	private ArrayList<String> fields;
	private TrafficListenerInterface trafficListener;

	private ViewTableModel tm;
	private JTable table;

	private JPanel buttons;
	private JLabel lblPagination;

	public ViewPanel(Oauth oauth, ServiceItem service, ArrayList<String> fields, TrafficListenerInterface trafficListener) throws Exception
	{
		this.oauth = oauth;
		this.service = service;
		this.fields = fields;
		this.trafficListener = trafficListener;


		this.setLayout(new BorderLayout());


		tm = new ViewTableModel(oauth, service.getUri(), trafficListener);

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


		// pagination
		buttons = new JPanel();

		lblPagination = new JLabel(tm.getStartIndex() + " / " + tm.getItemsPerPage() + " of " + tm.getTotalResults());
		JButton btnPrev = new JButton("Prev");
		JButton btnNext = new JButton("Next");

		buttons.setLayout(new FlowLayout(FlowLayout.CENTER));

		tm.addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) 
			{
				lblPagination = new JLabel(tm.getStartIndex() + " / " + tm.getItemsPerPage() + " of " + tm.getTotalResults());

				buttons.validate();
			}

		});

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

		buttons.add(btnPrev);
		buttons.add(lblPagination);
		buttons.add(btnNext);

		this.add(buttons, BorderLayout.SOUTH);
	}

	public ViewPanel(Oauth oauth, ServiceItem service) throws Exception
	{
		this(oauth, service, null, null);
	}

	public ViewPanel(Oauth oauth, ServiceItem service, TrafficListenerInterface trafficListener) throws Exception
	{
		this(oauth, service, null, trafficListener);
	}

	public ViewPanel(Oauth oauth, ServiceItem service, ArrayList<String> fields) throws Exception
	{
		this(oauth, service, fields, null);
	}
	
	public JTable getTable()
	{
		return table;
	}
}
