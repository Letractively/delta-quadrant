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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.k42b3.oat.ConfigFilter;

/**
 * oauth_config
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class OauthConfig extends ConfigFilter
{
	private JCheckBox ckb_active;
	private JTextField txt_consumer_key;
	private JTextField txt_consumer_secret;
	private JTextField txt_token;
	private JTextField txt_token_secret;
	private JComboBox cbo_method;
	
	public String getName()
	{
		return "OAuth";
	}

	public Properties onSave()
	{
		Properties props = new Properties();
		
		props.setProperty("consumer_key", this.txt_consumer_key.getText());
		props.setProperty("consumer_secret", this.txt_consumer_secret.getText());
		props.setProperty("token", this.txt_token.getText());
		props.setProperty("token_secret", this.txt_token_secret.getText());
		props.setProperty("method", this.cbo_method.getSelectedItem().toString());

		return props;
	}
	
	public boolean isActive()
	{
		return this.ckb_active.isSelected();
	}
	
	public OauthConfig()
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
		
		
		// consumer key
		JPanel panel_consumer_key = new JPanel();

		panel_consumer_key.setLayout(new FlowLayout());


		JLabel lbl_consumer_key = new JLabel("Consumer key:");

		lbl_consumer_key.setPreferredSize(new Dimension(100, 24));

		panel_consumer_key.add(lbl_consumer_key);


		this.txt_consumer_key = new JTextField();
		
		this.txt_consumer_key.setPreferredSize(new Dimension(200, 24));
		
		panel_consumer_key.add(this.txt_consumer_key);
		
		
		panel.add(panel_consumer_key);
		
		
		// consumer secret
		JPanel panel_consumer_secret = new JPanel();
		
		panel_consumer_secret.setLayout(new FlowLayout());
		
		
		JLabel lbl_consumer_secret = new JLabel("Consumer secret:");

		lbl_consumer_secret.setPreferredSize(new Dimension(100, 24));

		panel_consumer_secret.add(lbl_consumer_secret);


		this.txt_consumer_secret = new JTextField();
		
		this.txt_consumer_secret.setPreferredSize(new Dimension(200, 24));
		
		panel_consumer_secret.add(this.txt_consumer_secret);
		
		
		panel.add(panel_consumer_secret);
		
		
		// token
		JPanel panel_token = new JPanel();
		
		panel_token.setLayout(new FlowLayout());
		
		
		JLabel lbl_token = new JLabel("Token:");

		lbl_token.setPreferredSize(new Dimension(100, 24));

		panel_token.add(lbl_token);


		this.txt_token = new JTextField();
		
		this.txt_token.setPreferredSize(new Dimension(200, 24));
		
		panel_token.add(this.txt_token);
		
		
		panel.add(panel_token);
		
		
		// token secret
		JPanel panel_token_secret = new JPanel();
		
		panel_token_secret.setLayout(new FlowLayout());
		
		
		JLabel lbl_token_secret = new JLabel("Token secret:");

		lbl_token_secret.setPreferredSize(new Dimension(100, 24));

		panel_token_secret.add(lbl_token_secret);


		this.txt_token_secret = new JTextField();
		
		this.txt_token_secret.setPreferredSize(new Dimension(200, 24));
		
		panel_token_secret.add(this.txt_token_secret);
		
		
		panel.add(panel_token_secret);
		
		
		// method
		JPanel panel_method = new JPanel();
		
		panel_method.setLayout(new FlowLayout());
		
		
		JLabel lbl_method = new JLabel("Method:");

		lbl_method.setPreferredSize(new Dimension(100, 24));

		panel_method.add(lbl_method);


		String[] methods = {"PLAINTEXT", "HMACSHA1"};
		
		this.cbo_method = new JComboBox(methods);

		this.cbo_method.setPreferredSize(new Dimension(200, 24));
		
		panel_method.add(this.cbo_method);
		
		
		panel.add(panel_method);
		
		
		this.add(panel);
	}
}
