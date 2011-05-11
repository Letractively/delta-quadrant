/**
 * zubat
 * 
 * An java application to access the API of amun. It is used to debug and
 * control a website based on amun. This is the reference implementation 
 * howto access the api. So feel free to hack and extend.
 * 
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
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

package com.k42b3.zubat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.k42b3.zubat.oauth.OauthProvider;

/**
 * Login
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Login extends JFrame
{
	private Configuration config;
	private Services availableServices;
	private Oauth oauth;
	private Logger logger;

	public Login()
	{
		this.logger = Logger.getLogger("com.k42b3.zubat");

		this.setTitle("zubat (version: " + Zubat.version + ")");

		this.setLocation(100, 100);

		this.setSize(200, 100);

		this.setMinimumSize(this.getSize());

		this.setResizable(false);

		this.setLayout(new BorderLayout());


		// login button
		JButton btnLogin = new JButton("Login");
		
		btnLogin.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				if(oauth.requestToken())
				{
					if(oauth.authorizeToken())
					{
						if(oauth.accessToken())
						{
							// probably save the token in the config file
							System.out.println("Token: " + oauth.getToken());
							System.out.println("Token secret: " + oauth.getTokenSecret());

							JOptionPane.showMessageDialog(null, "You have successful authenticated");

							setVisible(false);

							System.exit(0);
						}
						else
						{
							logger.warning("Could not get access token");
						}
					}
					else
					{
						logger.warning("Could not authorize token");
					}
				}
				else
				{
					logger.warning("Could not get request token");
				}
			}

		});

		this.add(btnLogin);


		// oauth config
		try
		{
			config = Configuration.parseFile(new File("config.xml"));

			availableServices = new Services(config.getBaseUrl());

			String requestUrl = availableServices.getItem("http://oauth.net/core/1.0/endpoint/request").getUri();
			String authorizationUrl = availableServices.getItem("http://oauth.net/core/1.0/endpoint/authorize").getUri();
			String accessUrl = availableServices.getItem("http://oauth.net/core/1.0/endpoint/access").getUri();

			OauthProvider provider = new OauthProvider(requestUrl, authorizationUrl, accessUrl, config.getConsumerKey(), config.getConsumerSecret());
			oauth = new Oauth(provider);
		}
		catch(Exception e)
		{
			Zubat.handleException(e);
		}


		this.setVisible(true);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
