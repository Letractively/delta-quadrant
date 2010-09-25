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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.k42b3.espeon.model.template;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

/**
 * generate
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class generate extends JFrame
{
	public static boolean is_active = false;
	
	private HashMap<String, Object> table;
	
	private JTextField txt_name;
	private JTextField txt_ns;
	private JTextField txt_table;
	private JTextField txt_primary_key;

	private JButton btn_generate;
	private JButton btn_cancel;
	
	private template tm;

	private igenerate callback;

	public generate(HashMap<String, Object> table)
	{
		this.table = table;


		this.setLocationRelativeTo(null);

		this.setSize(200, 180);

		this.setMinimumSize(this.getSize());

		this.setResizable(false);

		this.setTitle("Generate");

		this.setLayout(new BorderLayout());


		JPanel panel = new JPanel();

		panel.setLayout(new GridLayout(0, 1));


		String table_name = table.get("table").toString();
		int pos = table_name.lastIndexOf('_');
		String name = table_name.substring(pos + 1);
		String ns = table_name.substring(0, pos);
		String table_primary_key = table.get("primary_key") != null ? table.get("primary_key").toString() : "";


		JPanel panel_name = new JPanel();

		panel_name.setLayout(new FlowLayout());

		JLabel lbl_name = new JLabel("Name");
		lbl_name.setPreferredSize(new Dimension(80, 20));

		this.txt_name = new JTextField();
		this.txt_name.setText(name);
		this.txt_name.setPreferredSize(new Dimension(120, 24));

		panel_name.add(lbl_name);
		panel_name.add(this.txt_name);
		
		panel.add(panel_name);


		JPanel panel_ns = new JPanel();

		panel_ns.setLayout(new FlowLayout());

		JLabel lbl_ns = new JLabel("Namespace");
		lbl_ns.setPreferredSize(new Dimension(80, 20));
		
		this.txt_ns = new JTextField();
		this.txt_ns.setText(ns);
		this.txt_ns.setPreferredSize(new Dimension(120, 24));
		
		panel_ns.add(lbl_ns);
		panel_ns.add(this.txt_ns);
		
		panel.add(panel_ns);


		JPanel panel_table = new JPanel();
		
		panel_table.setLayout(new FlowLayout());

		JLabel lbl_table = new JLabel("Table");
		lbl_table.setPreferredSize(new Dimension(80, 20));
		
		this.txt_table = new JTextField();
		this.txt_table.setText(table_name);
		this.txt_table.setPreferredSize(new Dimension(120, 24));
		
		panel_table.add(lbl_table);
		panel_table.add(this.txt_table);
		
		panel.add(panel_table);
		
		
		JPanel panel_primary_key = new JPanel();
		
		panel_primary_key.setLayout(new FlowLayout());

		JLabel lbl_primary_key = new JLabel("Primary Key");
		lbl_primary_key.setPreferredSize(new Dimension(80, 20));
		
		this.txt_primary_key = new JTextField();
		this.txt_primary_key.setText(table_primary_key);
		this.txt_primary_key.setPreferredSize(new Dimension(120, 24));
		
		panel_primary_key.add(lbl_primary_key);
		panel_primary_key.add(this.txt_primary_key);
		
		panel.add(panel_primary_key);
		
		
		this.add(panel, BorderLayout.NORTH);
		
		
		this.tm = new template();

		JScrollPane scr_table = new JScrollPane(new JTable(this.tm));
		
		scr_table.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));		
		
		scr_table.setPreferredSize(new Dimension(180, 120));
		
		this.add(scr_table, BorderLayout.CENTER);

		
		JPanel panel_buttons = new JPanel();
		
		panel_buttons.setLayout(new FlowLayout());
		
		this.btn_generate = new JButton("Generate");
		this.btn_generate.setPreferredSize(new Dimension(100, 24));
		this.btn_generate.addActionListener(new generate_handler());
		
		panel_buttons.add(this.btn_generate);
		
		this.btn_cancel = new JButton("Cancel");
		this.btn_cancel.setPreferredSize(new Dimension(100, 24));
		this.btn_cancel.addActionListener(new cancel_handler());
		
		panel_buttons.add(this.btn_cancel);
		
		this.add(panel_buttons, BorderLayout.SOUTH);
	}
	
	public void set_callback(igenerate callback)
	{
		this.callback = callback;
	}

	public void close()
	{
		setVisible(false);
		
		generate.is_active = false;
	}
	
	public class generate_handler implements ActionListener
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


			table.put("name", txt_name.getText());
			table.put("namespace", txt_ns.getText());


			callback.generate(templates, table);

			close();
		}
	}
	
	public class cancel_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			close();
		}
	}
}
