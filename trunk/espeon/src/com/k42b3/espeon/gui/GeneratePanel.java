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

package com.k42b3.espeon.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.k42b3.espeon.GenerateCallback;
import com.k42b3.espeon.model.FileTemplate;

/**
 * generate
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 142 $
 */
public class GeneratePanel extends JFrame
{
	public static boolean isActive = false;
	
	private HashMap<String, HashMap<String, Object>> tables;

	/*
	private JTextField txtName;
	private JTextField txtNs;
	private JTextField txtTable;
	private JTextField txtPrimaryKey;
	*/

	private JButton btnGenerate;
	private JButton btnCancel;
	
	private FileTemplate tm;

	private GenerateCallback callback;

	public GeneratePanel(HashMap<String, HashMap<String, Object>> tables)
	{
		this.tables = tables;


		this.setLocationRelativeTo(null);

		this.setSize(200, 200);

		this.setMinimumSize(this.getSize());

		this.setResizable(false);

		this.setTitle("Generate");

		this.setLayout(new BorderLayout());


		/*
		JPanel panel = new JPanel();

		panel.setLayout(new GridLayout(0, 1));


		String tableName = table.get("table").toString();
		int pos = tableName.lastIndexOf('_');
		String name = GeneratePanel.convertTableToClass(tableName.substring(pos + 1));
		String ns = GeneratePanel.convertTableToClass(tableName.substring(0, pos));
		String tablePrimaryKey = table.get("primaryKey") != null ? table.get("primaryKey").toString() : "";


		JPanel panelName = new JPanel();

		panelName.setLayout(new FlowLayout());

		JLabel lblName = new JLabel("Name");
		lblName.setPreferredSize(new Dimension(80, 20));

		this.txtName = new JTextField();
		this.txtName.setText(name);
		this.txtName.setPreferredSize(new Dimension(120, 24));

		panelName.add(lblName);
		panelName.add(this.txtName);
		
		panel.add(panelName);


		JPanel panelNs = new JPanel();

		panelNs.setLayout(new FlowLayout());

		JLabel lblNs = new JLabel("Namespace");
		lblNs.setPreferredSize(new Dimension(80, 20));
		
		this.txtNs = new JTextField();
		this.txtNs.setText(ns);
		this.txtNs.setPreferredSize(new Dimension(120, 24));
		
		panelNs.add(lblNs);
		panelNs.add(this.txtNs);
		
		panel.add(panelNs);


		JPanel panelTable = new JPanel();
		
		panelTable.setLayout(new FlowLayout());

		JLabel lblTable = new JLabel("Table");
		lblTable.setPreferredSize(new Dimension(80, 20));
		
		this.txtTable = new JTextField();
		this.txtTable.setText(tableName);
		this.txtTable.setPreferredSize(new Dimension(120, 24));
		
		panelTable.add(lblTable);
		panelTable.add(this.txtTable);
		
		panel.add(panelTable);
		
		
		JPanel panelPrimaryKey = new JPanel();
		
		panelPrimaryKey.setLayout(new FlowLayout());

		JLabel lblPrimaryKey = new JLabel("Primary Key");
		lblPrimaryKey.setPreferredSize(new Dimension(80, 20));
		
		this.txtPrimaryKey = new JTextField();
		this.txtPrimaryKey.setText(tablePrimaryKey);
		this.txtPrimaryKey.setPreferredSize(new Dimension(120, 24));
		
		panelPrimaryKey.add(lblPrimaryKey);
		panelPrimaryKey.add(this.txtPrimaryKey);
		
		panel.add(panelPrimaryKey);
		
		
		this.add(panel, BorderLayout.NORTH);
		*/


		this.tm = new FileTemplate();

		JScrollPane scrTable = new JScrollPane(new JTable(this.tm));
		
		scrTable.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));		
		
		scrTable.setPreferredSize(new Dimension(180, 120));
		
		this.add(scrTable, BorderLayout.CENTER);

		
		JPanel panelButtons = new JPanel();
		
		panelButtons.setLayout(new FlowLayout());
		
		this.btnGenerate = new JButton("Generate");
		this.btnGenerate.setPreferredSize(new Dimension(100, 24));
		this.btnGenerate.addActionListener(new generateHandler());
		
		panelButtons.add(this.btnGenerate);
		
		this.btnCancel = new JButton("Cancel");
		this.btnCancel.setPreferredSize(new Dimension(100, 24));
		this.btnCancel.addActionListener(new cancelHandler());
		
		panelButtons.add(this.btnCancel);
		
		this.add(panelButtons, BorderLayout.SOUTH);
	}
	
	public void setCallback(GenerateCallback callback)
	{
		this.callback = callback;
	}

	public void close()
	{
		setVisible(false);
		
		GeneratePanel.isActive = false;
	}

	public class generateHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			ArrayList<String> templates = new ArrayList<String>();

			for(int i = 0; i < tm.getRowCount(); i++)
			{
				if((Boolean) tm.getValueAt(i, 0))
				{
					templates.add((String) tm.getValueAt(i, 1));
				}
			}

			if(templates.size() > 0)
			{
				try
				{
					callback.onGenerate(templates, tables);

					JOptionPane.showMessageDialog(null, "You have successful generated the code", "Informations", JOptionPane.INFORMATION_MESSAGE);
				}
				catch(Exception ex)
				{
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}

				close();
			}
			else
			{
				JOptionPane.showMessageDialog(null, "You must select min one template", "Information", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public class cancelHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			close();
		}
	}
}
