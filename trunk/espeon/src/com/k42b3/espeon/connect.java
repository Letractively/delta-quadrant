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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * connect
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class connect extends JFrame
{
	private JTextField txt_host;
	private JTextField txt_db;
	private JTextField txt_user;
	private JTextField txt_pw;

	private JButton btn_connect;
	private JButton btn_cancel;
	
	private icallback callback;
	
	public connect()
	{
		this.setLocationRelativeTo(null);
		
		this.setSize(200, 180);

		this.setMinimumSize(this.getSize());

		this.setLayout(new GridLayout(0, 1));


		JPanel panel_host = new JPanel();

		panel_host.setLayout(new FlowLayout());

		JLabel lbl_host = new JLabel("Host:");
		lbl_host.setPreferredSize(new Dimension(100, 20));
		
		this.txt_host = new JTextField();
		this.txt_host.setText("localhost");
		this.txt_host.setPreferredSize(new Dimension(100, 20));
		
		panel_host.add(lbl_host);
		panel_host.add(this.txt_host);
		
		this.add(panel_host);
		
		
		JPanel panel_db = new JPanel();
		
		panel_db.setLayout(new FlowLayout());

		JLabel lbl_db = new JLabel("Database:");
		lbl_db.setPreferredSize(new Dimension(100, 20));
		
		this.txt_db = new JTextField();
		this.txt_db.setText("cms");
		this.txt_db.setPreferredSize(new Dimension(100, 20));
		
		panel_db.add(lbl_db);
		panel_db.add(this.txt_db);
		
		this.add(panel_db);
		
		
		JPanel panel_user = new JPanel();
		
		panel_user.setLayout(new FlowLayout());

		JLabel lbl_user = new JLabel("User:");
		lbl_user.setPreferredSize(new Dimension(100, 20));
		
		this.txt_user = new JTextField();
		this.txt_user.setText("root");
		this.txt_user.setPreferredSize(new Dimension(100, 20));
		
		panel_user.add(lbl_user);
		panel_user.add(this.txt_user);
		
		this.add(panel_user);
		

		JPanel panel_pw = new JPanel();
		
		panel_pw.setLayout(new FlowLayout());

		JLabel lbl_pw = new JLabel("Password:");
		lbl_pw.setPreferredSize(new Dimension(100, 20));
		
		this.txt_pw = new JTextField();
		this.txt_pw.setPreferredSize(new Dimension(100, 20));
		
		panel_pw.add(lbl_pw);
		panel_pw.add(this.txt_pw);
		
		this.add(panel_pw);
		
		
		JPanel panel_buttons = new JPanel();
		
		panel_buttons.setLayout(new FlowLayout());
		
		this.btn_connect = new JButton("Connect");
		this.btn_connect.setPreferredSize(new Dimension(100, 24));
		this.btn_connect.addActionListener(new connect_handler());
		
		panel_buttons.add(this.btn_connect);
		
		this.btn_cancel = new JButton("Cancel");
		this.btn_cancel.setPreferredSize(new Dimension(100, 24));
		this.btn_cancel.addActionListener(new cancel_handler());
		
		panel_buttons.add(this.btn_cancel);
		
		this.add(panel_buttons);
	}
	
	public void set_callback(icallback callback)
	{
		this.callback = callback;
	}

	public class connect_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			callback.connect(txt_host.getText(), txt_db.getText(), txt_user.getText(), txt_pw.getText());

			setVisible(false);
		}
	}
	
	public class cancel_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			setVisible(false);
		}
	}
}
