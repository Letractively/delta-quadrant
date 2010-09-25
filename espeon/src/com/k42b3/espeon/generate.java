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
		
		this.setLayout(new BorderLayout());


		JPanel panel_name = new JPanel();

		panel_name.setLayout(new FlowLayout());

		this.txt_ns = new JTextField();
		this.txt_ns.setText("psx_data");
		this.txt_ns.setPreferredSize(new Dimension(120, 20));
		
		JLabel lbl_underscore = new JLabel("_");
		
		this.txt_name = new JTextField();
		this.txt_name.setText("user");
		this.txt_name.setPreferredSize(new Dimension(64, 20));
		
		panel_name.add(this.txt_ns);
		panel_name.add(lbl_underscore);
		panel_name.add(this.txt_name);
		
		this.add(panel_name, BorderLayout.NORTH);


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
