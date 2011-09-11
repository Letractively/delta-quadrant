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
	private JCheckBox ckbActive;
	private JTextField txtConsumerKey;
	private JTextField txtConsumerSecret;
	private JTextField txtToken;
	private JTextField txtTokenSecret;
	private JComboBox cboMethod;

	public String getName()
	{
		return "OAuth";
	}

	public Properties onSave()
	{
		Properties props = new Properties();

		props.setProperty("consumer_key", this.txtConsumerKey.getText());
		props.setProperty("consumer_secret", this.txtConsumerSecret.getText());
		props.setProperty("token", this.txtToken.getText());
		props.setProperty("token_secret", this.txtTokenSecret.getText());
		props.setProperty("method", this.cboMethod.getSelectedItem().toString());

		return props;
	}
	
	public boolean isActive()
	{
		return this.ckbActive.isSelected();
	}
	
	public OauthConfig()
	{
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));


		// active
		JPanel panelActive = new JPanel();
		panelActive.setLayout(new FlowLayout());

		JLabel lblActive = new JLabel("Active:");
		lblActive.setPreferredSize(new Dimension(100, 24));
		panelActive.add(lblActive);

		this.ckbActive = new JCheckBox();
		this.ckbActive.setPreferredSize(new Dimension(200, 24));
		panelActive.add(this.ckbActive);

		panel.add(panelActive);


		// consumer key
		JPanel panelConsumerKey = new JPanel();
		panelConsumerKey.setLayout(new FlowLayout());

		JLabel lblConsumerKey = new JLabel("Consumer key:");
		lblConsumerKey.setPreferredSize(new Dimension(100, 24));
		panelConsumerKey.add(lblConsumerKey);

		this.txtConsumerKey = new JTextField();
		this.txtConsumerKey.setPreferredSize(new Dimension(200, 24));
		panelConsumerKey.add(this.txtConsumerKey);

		panel.add(panelConsumerKey);


		// consumer secret
		JPanel panelConsumerSecret = new JPanel();
		panelConsumerSecret.setLayout(new FlowLayout());

		JLabel lblConsumerSecret = new JLabel("Consumer secret:");
		lblConsumerSecret.setPreferredSize(new Dimension(100, 24));
		panelConsumerSecret.add(lblConsumerSecret);

		this.txtConsumerSecret = new JTextField();
		this.txtConsumerSecret.setPreferredSize(new Dimension(200, 24));
		panelConsumerSecret.add(this.txtConsumerSecret);

		panel.add(panelConsumerSecret);


		// token
		JPanel panelToken = new JPanel();
		panelToken.setLayout(new FlowLayout());

		JLabel lblToken = new JLabel("Token:");
		lblToken.setPreferredSize(new Dimension(100, 24));
		panelToken.add(lblToken);

		this.txtToken = new JTextField();
		this.txtToken.setPreferredSize(new Dimension(200, 24));
		panelToken.add(this.txtToken);

		panel.add(panelToken);


		// token secret
		JPanel panelTokenSecret = new JPanel();
		panelTokenSecret.setLayout(new FlowLayout());

		JLabel lblTokenSecret = new JLabel("Token secret:");
		lblTokenSecret.setPreferredSize(new Dimension(100, 24));
		panelTokenSecret.add(lblTokenSecret);

		this.txtTokenSecret = new JTextField();
		this.txtTokenSecret.setPreferredSize(new Dimension(200, 24));
		panelTokenSecret.add(this.txtTokenSecret);

		panel.add(panelTokenSecret);


		// method
		JPanel panelMethod = new JPanel();
		panelMethod.setLayout(new FlowLayout());

		JLabel lblMethod = new JLabel("Method:");
		lblMethod.setPreferredSize(new Dimension(100, 24));
		panelMethod.add(lblMethod);

		String[] methods = {"PLAINTEXT", "HMAC-SHA1"};
		this.cboMethod = new JComboBox(methods);
		this.cboMethod.setPreferredSize(new Dimension(200, 24));
		panelMethod.add(this.cboMethod);

		panel.add(panelMethod);


		this.add(panel);
	}
}
