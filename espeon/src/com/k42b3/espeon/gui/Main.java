package com.k42b3.espeon.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.k42b3.espeon.ConnectCallback;
import com.k42b3.espeon.Espeon;
import com.k42b3.espeon.GenerateCallback;
import com.k42b3.espeon.Toolbar;
import com.k42b3.espeon.View;
import com.k42b3.espeon.model.SqlTable;

public class Main extends JFrame implements View
{
	private JList<String> list;
	private DefaultListModel<String> lm;
	private JTable table;
	private SqlTable tm;
	private Toolbar toolbar;

	private Espeon inst;
	private ConnectCallback connectCb;
	private GenerateCallback generateCb;

	public Main(Espeon inst)
	{
		this.inst = inst;


		this.setTitle("espeon (version: " + Espeon.version + ")");
		
		this.setLocation(100, 100);
		
		this.setSize(700, 500);
		
		this.setMinimumSize(this.getSize());

		this.setLayout(new BorderLayout());


		// columns
		this.table = new JTable();

		this.tm = new SqlTable(inst);

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
		this.lm = new DefaultListModel<String>(); 
		
		this.list = new JList<String>(this.lm);

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
	}
	
	public void setConnectCallback(ConnectCallback connectCb)
	{
		this.connectCb = connectCb;
	}

	public void setGenerateCallback(GenerateCallback generateCb)
	{
		this.generateCb = generateCb;
	}
	
	public class connectHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			SwingUtilities.invokeLater(new Runnable(){

				public void run() 
				{
					if(!ConnectPanel.isActive)
					{
						ConnectPanel win = new ConnectPanel();

						win.pack();

						win.setCallback(new ConnectCallback(){

							public void onConnect(String host, String db, String user, String pw) throws Exception
							{
								try
								{
									// connect
									connectCb.onConnect(host, db, user, pw);


									// list tables
									PreparedStatement ps = inst.getConnection().prepareStatement("SHOW TABLES");

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

							}

						});

						win.setVisible(true);
					}
				}

			});
		}
	}

	public class generateHandler implements ActionListener
	{
		private HashMap<String, HashMap<String, Object>> tables;

		public void actionPerformed(ActionEvent e)
		{
			if(list.getSelectedIndex() != -1)
			{
				tables = new HashMap<String, HashMap<String, Object>>();

				List<String> selectedTables = list.getSelectedValuesList();

				for(int i = 0; i < selectedTables.size(); i++)
				{
					try
					{
						String table = selectedTables.get(i);
						HashMap<String, Object> params = inst.getParams(table);

						tables.put(table, params);
					}
					catch(Exception ex)
					{
						Espeon.handleException(ex);
					}
				}

				SwingUtilities.invokeLater(new Runnable(){

					public void run() 
					{
						GeneratePanel win = new GeneratePanel(tables);

						win.pack();

						win.setCallback(generateCb);

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
			JOptionPane.showMessageDialog(null, Espeon.getAbout(), "About", JOptionPane.INFORMATION_MESSAGE);
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

				tm.loadTable(list.getSelectedValue().toString());
			}
		}
	}
}
