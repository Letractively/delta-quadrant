/**
 * espeon
 * 
 * An application to generate php classes for the psx framework (phpsx.org). It
 * should make development of new applications easier and with fewer errors.
 * You can connect to a mysql database select a tabel and generate the php 
 * classes
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of espeon. espeon is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * espeon is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with espeon. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.espeon;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * espeon
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class espeon extends JFrame
{
	public static String ver = "0.0.1 beta";
	
	private JList list;
	private DefaultListModel lm;
	private JTable table;
	private table tm;
	private toolbar toolbar;
	
	private Connection con;
	
	public espeon()
	{
		this.setTitle("espeon (version: " + ver + ")");
		
		this.setLocation(100, 100);
		
		this.setSize(600, 500);
		
		this.setMinimumSize(this.getSize());

		this.setLayout(new BorderLayout());


		// columns
		JPanel panel_columns = new JPanel();

		this.table = new JTable();

		this.tm = new table();

		this.table.setModel(this.tm);

		this.table.setEnabled(false);
		
		JScrollPane scr_table = new JScrollPane(this.table);
		
		scr_table.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 4));		
		
		this.add(scr_table, BorderLayout.CENTER);


		// toolbar
		this.toolbar = new toolbar();
		
		this.toolbar.get_connect().addActionListener(new connect_handler());
		
		this.toolbar.get_generate().addActionListener(new generate_handler());
		
		this.toolbar.get_generate().setEnabled(false);
		
		this.toolbar.get_about().addActionListener(new about_handler());
		
		this.toolbar.get_exit().addActionListener(new exit_handler());

		this.add(this.toolbar, BorderLayout.SOUTH);


		// list
		this.lm = new DefaultListModel(); 
		
		this.list = new JList(this.lm);

		this.list.setEnabled(false);
		
		//this.list.setModel(new file_list());

		this.list.addListSelectionListener(new list_handler());

		JScrollPane scr_list = new JScrollPane(this.list);
		
		scr_list.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		scr_list.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		scr_list.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scr_list.setPreferredSize(new Dimension(128, 400));
		
		this.add(scr_list, BorderLayout.WEST);

		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public class connect_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			SwingUtilities.invokeLater(new Runnable(){

				public void run() 
				{
					connect win = new connect();

					win.pack();

					win.set_callback(new icallback(){

						public void connect(String host, String db, String user, String pw) {

							try
							{
								// connect
								con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + db + "?user=" + user + "&amp;password=" + pw);


								// list tables
								PreparedStatement ps = con.prepareStatement("SHOW TABLES");

								ps.execute();

								ResultSet result = ps.getResultSet();

								while(result.next())
								{
									lm.addElement(result.getString(1));
								}


								// enable/disable buttons
								toolbar.get_generate().setEnabled(true);
								toolbar.get_connect().setEnabled(false);
								
								list.setEnabled(true);
								table.setEnabled(true);
							}
							catch(SQLException e)
							{
								JOptionPane.showMessageDialog(null, e.getMessage(), "SQL Exception", JOptionPane.WARNING_MESSAGE);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}

						}

					});

					win.setVisible(true);

				}

			});
		}
	}

	public class generate_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			ArrayList<String> columns = new ArrayList<String>();

			for(int i = 0; i < tm.getRowCount(); i++)
			{
				if((Boolean) tm.getValueAt(i, 0))
				{
					columns.add(tm.getValueAt(i, 1).toString());
				}
			}
			
			
			SwingUtilities.invokeLater(new Runnable(){

				public void run() 
				{
					generate win = new generate();

					win.pack();
					
					win.setVisible(true);
				}
				
			});
		}
	}
	
	public class about_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			StringBuilder out = new StringBuilder();
			
			out.append("espeon version " + ver + "\n");
			out.append("author: Christoph \"k42b3\" Kappestein" + "\n");
			out.append("website: http://code.google.com/p/delta-quadrant" + "\n");
			out.append("license: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
			out.append("\n");
			out.append("This application generates record classes for the psx framework (phpsx.org)" + "\n");
			out.append("to make the development of new applications easier and with fewer errors." + "\n");
			out.append("You can connect to a mysql database select a tabel and generate the php classes" + "\n");
			out.append("\n");
			out.append("best regards\n");
			out.append("k42b3\n");
			
			JOptionPane.showMessageDialog(null, out, "About", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public class exit_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			System.exit(0);
		}
	}
	
	public class list_handler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e) 
		{
			JList list = (JList) e.getSource();
			
			tm.load_table(con, list.getSelectedValue().toString());
		}
	}
}
