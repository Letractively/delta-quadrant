/**
 * espeon
 * 
 * With espeon you can generate sourcecode from database structures. It was 
 * mainly developed to generate PHP classes for the psx framework (phpsx.org) 
 * but because it uses a template engine (FreeMarker) you can use it for any 
 * purpose you like.
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
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
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

import com.k42b3.espeon.model.Table;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * espeon
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Espeon extends JFrame
{
	public static String ver = "0.0.3 beta";
	public static String path = "templates";

	private JList list;
	private DefaultListModel lm;
	private JTable table;
	private Table tm;
	private Toolbar toolbar;
	
	private Connection con;
	
	private Configuration cfg;
	
	public Espeon()
	{
		this.setTitle("espeon (version: " + ver + ")");
		
		this.setLocation(100, 100);
		
		this.setSize(700, 500);
		
		this.setMinimumSize(this.getSize());

		this.setLayout(new BorderLayout());


		// columns
		JPanel panelColumns = new JPanel();

		this.table = new JTable();

		this.tm = new Table();

		this.table.setModel(this.tm);

		this.table.setEnabled(false);
		
		JScrollPane scrTable = new JScrollPane(this.table);
		
		scrTable.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 4));		
		
		this.add(scrTable, BorderLayout.CENTER);


		// toolbar
		this.toolbar = new Toolbar();
		
		this.toolbar.getConnect().addActionListener(new connectHandler());
		
		this.toolbar.getGenerate().addActionListener(new generateHandler());
		
		this.toolbar.getGenerate().setEnabled(false);
		
		this.toolbar.getAbout().addActionListener(new aboutHandler());
		
		this.toolbar.getExit().addActionListener(new exitHandler());

		this.add(this.toolbar, BorderLayout.SOUTH);


		// list
		this.lm = new DefaultListModel(); 
		
		this.list = new JList(this.lm);

		this.list.setEnabled(false);
		
		//this.list.setModel(new file_list());

		this.list.addListSelectionListener(new listHandler());

		JScrollPane scrList = new JScrollPane(this.list);
		
		scrList.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		scrList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		scrList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrList.setPreferredSize(new Dimension(180, 400));

		this.add(scrList, BorderLayout.WEST);


		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		// set template config
		try
		{
			File templatePath = new File(Espeon.path);
			
			if(templatePath.isDirectory())
			{
				this.cfg = new Configuration();

				this.cfg.setDirectoryForTemplateLoading(new File(Espeon.path));

				this.cfg.setObjectWrapper(new DefaultObjectWrapper());
			}
			else
			{
				throw new Exception("You have to create a dir called '" + Espeon.path + "' where the templates are located");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public class connectHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			SwingUtilities.invokeLater(new Runnable(){

				public void run() 
				{
					Connect win = new Connect();

					win.pack();

					win.setCallback(new ConnectInterface(){

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
								toolbar.getGenerate().setEnabled(true);
								toolbar.getConnect().setEnabled(false);
								
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

	public class generateHandler implements ActionListener
	{
		private HashMap<String, Object> table;

		public void actionPerformed(ActionEvent e) 
		{
			if(list.getSelectedIndex() != -1)
			{
				this.table = new HashMap<String, Object>();

				Object firstColumn = "";
				Object lastColumn = "";
				Object primaryKey = "";
				ArrayList<Object> unqiueKey = new ArrayList<Object>();
				ArrayList<Object> fields = new ArrayList<Object>();
				ArrayList<HashMap<String, String>> columns = new ArrayList<HashMap<String, String>>();

				for(int i = 0; i < tm.getRowCount(); i++)
				{
					if((Boolean) tm.getValueAt(i, 0))
					{
						String rField = tm.getValueAt(i, 1) != null ? tm.getValueAt(i, 1).toString() : "";
						String rType = tm.getValueAt(i, 2) != null ? tm.getValueAt(i, 2).toString() : "";
						String rNull = tm.getValueAt(i, 3) != null ? tm.getValueAt(i, 3).toString() : "";
						String rKey = tm.getValueAt(i, 4) != null ? tm.getValueAt(i, 4).toString() : "";
						String rDefault = tm.getValueAt(i, 5) != null ? tm.getValueAt(i, 5).toString() : "";
						String rExtra = tm.getValueAt(i, 6) != null ? tm.getValueAt(i, 6).toString() : "";


						lastColumn = rField;

						if(i == 0)
						{
							firstColumn = rField;
						}

						if(rKey.equals("PRI"))
						{
							primaryKey = rField;
						}

						if(rKey.equals("UNI"))
						{
							unqiueKey.add(rField);
						}


						fields.add(rField);


						String rLength = "";
						int pos = rType.indexOf('(');

						if(pos != -1)
						{
							String rawLength = rType.substring(pos + 1);
							rLength = rawLength.substring(0, rawLength.length() - 1);
							rType = rType.substring(0, pos);
						}

						HashMap<String, String> c = new HashMap<String, String>();

						c.put("field", rField);
						c.put("type", rType);
						c.put("length", rLength);
						c.put("null", rNull);
						c.put("key", rKey);
						c.put("default", rDefault);
						c.put("extra", rExtra);

						columns.add(c);
					}
				}

				this.table.put("table", list.getSelectedValue());
				this.table.put("firstColumn", firstColumn);
				this.table.put("lastColumn", lastColumn);
				this.table.put("primaryKey", primaryKey);
				this.table.put("unqiueKey", unqiueKey);
				this.table.put("fields", fields);
				this.table.put("columns", columns);


				SwingUtilities.invokeLater(new Runnable(){

					public void run() 
					{
						Generate win = new Generate(table);

						win.pack();
						
						win.setCallback(new GenerateInterface(){

							public void generate(ArrayList<String> templates, HashMap<String, Object> table)
							{
								try
								{
									/*
									JFileChooser jfc = new JFileChooser();
									jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

									if(jfc.showSaveDialog(espeon.this) == JFileChooser.APPROVE_OPTION)
									{
									}
									*/

									for(int i = 0; i < templates.size(); i++)
									{
										Template temp = cfg.getTemplate(templates.get(i));

										Writer out = new FileWriter(templates.get(i));

										temp.process(table, out);

										out.flush();
									}

									JOptionPane.showMessageDialog(null, "You have successful generated the code", "Informations", JOptionPane.INFORMATION_MESSAGE);
								}
								catch(Exception ex)
								{
									ex.printStackTrace();
								}
							}

						});

						win.setVisible(true);
					}
					
				});
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Please select a table", "Information", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	
	public class aboutHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			StringBuilder out = new StringBuilder();

			out.append("Version: espeon " + ver + "\n");
			out.append("Author: Christoph \"k42b3\" Kappestein" + "\n");
			out.append("Website: http://code.google.com/p/delta-quadrant" + "\n");
			out.append("License: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
			out.append("\n");
			out.append("With espeon you can generate sourcecode from database structures. It was" + "\n");
			out.append("mainly developed to generate PHP classes for the PSX framework (phpsx.org)" + "\n");
			out.append("but because it uses a template engine (FreeMarker) you can use it for any" + "\n");
			out.append("purpose you like." + "\n");

			JOptionPane.showMessageDialog(null, out, "About", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public class exitHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			System.exit(0);
		}
	}
	
	public class listHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e) 
		{
			if(e.getValueIsAdjusting())
			{
				JList list = (JList) e.getSource();
				
				tm.loadTable(con, list.getSelectedValue().toString());
			}
		}
	}
}
