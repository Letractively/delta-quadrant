/**
 * oat
 * 
 * An application with that you can make raw http requests to any url. You can 
 * save a request for later use. The application uses the java nio library to 
 * make non-blocking requests so the requests should work fluently.
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
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

package com.k42b3.oat.http.filterRequest;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.k42b3.oat.ConfigFilter;

/**
 * basicauth_config
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class BasicAuthConfig extends ConfigFilter
{
	private JCheckBox ckb_active;
	private JTextField txt_user;
	private JTextField txt_pw;
	
	public String getName()
	{
		return "Basic authentication";
	}
	
	public Properties onSave() 
	{
		Properties props = new Properties();
		
		props.setProperty("user", this.txt_user.getText());
		props.setProperty("pw", this.txt_pw.getText());

		return props;
	}
	
	public boolean isActive()
	{
		return this.ckb_active.isSelected();
	}

	public BasicAuthConfig()
	{
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JPanel panel = new JPanel();
		
		panel.setLayout(new GridLayout(0, 1));
		
		
		// active
		JPanel panel_active = new JPanel();

		panel_active.setLayout(new FlowLayout());


		JLabel lbl_active = new JLabel("Active:");

		lbl_active.setPreferredSize(new Dimension(100, 24));

		panel_active.add(lbl_active);


		this.ckb_active = new JCheckBox();
		
		this.ckb_active.setPreferredSize(new Dimension(200, 24));
		
		panel_active.add(this.ckb_active);
		
		
		panel.add(panel_active);
		
		
		// user
		JPanel panel_user = new JPanel();

		panel_user.setLayout(new FlowLayout());


		JLabel lbl_user = new JLabel("User:");

		lbl_user.setPreferredSize(new Dimension(100, 24));

		panel_user.add(lbl_user);


		this.txt_user = new JTextField();
		
		this.txt_user.setPreferredSize(new Dimension(200, 24));
		
		panel_user.add(this.txt_user);
		
		
		panel.add(panel_user);
		
		
		// password
		JPanel panel_pw = new JPanel();

		panel_pw.setLayout(new FlowLayout());


		JLabel lbl_pw = new JLabel("Password:");

		lbl_pw.setPreferredSize(new Dimension(100, 24));

		panel_pw.add(lbl_pw);


		this.txt_pw = new JTextField();
		
		txt_pw.setPreferredSize(new Dimension(200, 24));
		
		panel_pw.add(txt_pw);
		
		
		panel.add(panel_pw);
		
		
		this.add(panel);
	}
}
