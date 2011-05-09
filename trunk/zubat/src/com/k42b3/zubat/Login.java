package com.k42b3.zubat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Login extends JFrame
{
	private Logger logger;
	private CallbackInterface cb;

	public Login()
	{
		logger = Logger.getLogger("com.k42b3.zubat");
		
		this.setTitle("zubat (version: " + Zubat.version + ")");

		this.setLocation(100, 100);

		this.setSize(200, 100);

		this.setMinimumSize(this.getSize());

		this.setResizable(false);

		this.setLayout(new BorderLayout());


		JButton btn_login = new JButton("Login");
		
		btn_login.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				if(Zubat.getOauth().requestToken())
				{
					if(Zubat.getOauth().authorizeToken())
					{
						if(Zubat.getOauth().accessToken())
						{
							if(cb != null)
							{
								cb.call();
							}

							setVisible(false);
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

		this.add(btn_login);


		this.setVisible(true);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setCallback(CallbackInterface cb)
	{
		this.cb = cb;
	}
}
