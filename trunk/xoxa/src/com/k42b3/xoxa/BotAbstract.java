/**
 * xoxa
 * 
 * An IRC bot wich you can configure via an XML file. The bot can create 
 * multiple users wich can join a specific channel. The main task of the bot
 * is to deliver real time messages from different sources (feeds, twitter,
 * gmail, etc.). It pushs every x seconds for new resources and post it directly
 * to the channel if anything is new.
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

package com.k42b3.xoxa;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCEventListener;
import org.schwering.irc.lib.IRCModeParser;
import org.schwering.irc.lib.IRCUser;
import org.schwering.irc.lib.ssl.SSLIRCConnection;
import org.schwering.irc.lib.ssl.SSLTrustManager;

/**
 * BotAbstract
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
abstract public class BotAbstract extends Thread
{
	protected IRCConnection conn;
	protected String target;
	protected Logger logger;
	protected BotAbstract self;

	protected String host;
	protected int port;
	protected String nick;
	protected String pass;
	protected String channel;
	protected boolean ssl;

	private Date lastUpdated = new Date();

	private int minInterval = 300000;
	private int maxInterval = 600000;

	public BotAbstract(String host, int port, String nick, String pass, String channel, boolean ssl, int minInterval, int maxInterval)
	{
		try
		{
			this.host = host;
			this.port = port;
			this.nick = nick;
			this.pass = pass;
			this.channel = channel;
			this.ssl = ssl;
			this.minInterval = minInterval;
			this.maxInterval = maxInterval;

			String user = nick;
			String name = nick;

			this.self = this;
			this.logger = Logger.getLogger("com.k42b3.xoxa.bot");
			this.target = channel;

			if(!ssl)
			{
				conn = new IRCConnection(host, new int[] { port }, pass, nick, user, name);
			}
			else 
			{
				conn = new SSLIRCConnection(host, new int[] { port }, pass, nick, user, name);

				((SSLIRCConnection) conn).addTrustManager(new TrustManager());
			}

			conn.addIRCEventListener(new Listener());
			conn.setEncoding("UTF-8");
			conn.setPong(true);
			conn.setDaemon(false);
			conn.setColors(false);
			conn.connect();

			if(conn.isConnected())
			{
				logger.info("Bot connected to " + host);
				
				this.setDaemon(true);
				this.start();
			}
			else
			{
				logger.warning("Bot is not connected to " + host);
			}
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public void run()
	{
		conn.doJoin(target);

		logger.info("Bot joined channel " + target);

		try
		{
			while(true)
			{
				ArrayList<Resource> resources = this.getResources(16);

				if(resources != null)
				{
					this.lastUpdated = new Date();

					logger.info("Fetched " + resources.size() + " resources");

					for(int i = 0; i < resources.size(); i++)
					{
						conn.doPrivmsg(target, resources.get(i).getTitle());

						// wait 2 seconds between each message
						Thread.sleep(2000);
					}
				}
				else
				{
					logger.info("Fetched no resources");
				}

				// wait 
				int wait = this.minInterval + (int) (Math.random() * (this.maxInterval - this.minInterval) + 0.5);

				Thread.sleep(wait);
			}
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public Date getLastUpdated()
	{
		return this.lastUpdated;
	}

	abstract public ArrayList<Resource> getResources(int limit);

	public class TrustManager implements SSLTrustManager 
	{
		private X509Certificate[] chain;

		public X509Certificate[] getAcceptedIssuers() 
		{
			return chain != null ? chain : new X509Certificate[0];
		}

		public boolean isTrusted(X509Certificate[] chain)
		{
			this.chain = chain;
			
			return true;
		}
	}
	
	public class Listener implements IRCEventListener
	{
		public void onRegistered()
		{
			self.onRegistered();
		}

		public void onDisconnected()
		{
			self.onDisconnected();
		}

		public void onError(String msg)
		{
			self.onError(msg);
		}

		public void onError(int num, String msg)
		{
			self.onError(num, msg);
		}

		public void onInvite(String chan, IRCUser u, String nickPass)
		{
			self.onInvite(chan, u, nickPass);
		}

		public void onJoin(String chan, IRCUser u)
		{
			self.onJoin(chan, u);
		}

		public void onKick(String chan, IRCUser u, String nickPass, String msg)
		{
			self.onKick(chan, u, nickPass, msg);
		}

		public void onMode(IRCUser u, String nickPass, String mode)
		{
			self.onMode(u, nickPass, mode);
		}

		public void onMode(String chan, IRCUser u, IRCModeParser mp) 
		{
			self.onMode(chan, u, mp);
		}

		public void onNick(IRCUser u, String nickNew) 
		{
			self.onNick(u, nickNew);
		}

		public void onNotice(String target, IRCUser u, String msg) 
		{
			self.onNotice(target, u, msg);
		}

		public void onPart(String chan, IRCUser u, String msg) 
		{
			self.onPart(chan, u, msg);
		}

		public void onPrivmsg(String chan, IRCUser u, String msg) 
		{
			self.onPart(chan, u, msg);
		}

		public void onQuit(IRCUser u, String msg) 
		{
			self.onQuit(u, msg);
		}

		public void onReply(int num, String value, String msg) 
		{
			self.onReply(num, value, msg);
		}

		public void onTopic(String chan, IRCUser u, String topic) 
		{
			self.onTopic(chan, u, topic);
		}

		public void onPing(String p) 
		{
		}

		public void unknown(String a, String b, String c, String d) 
		{
			self.unknown(a, b, c, d);
		}
	}

	public void onRegistered()
	{
		System.out.println("Connected");
	}

	public void onDisconnected()
	{
		System.out.println("Disconnected");
	}

	public void onError(String msg)
	{
		System.out.println("Error: " + msg);
	}

	public void onError(int num, String msg)
	{
		System.out.println("Error #" + num + ": " + msg);
	}

	public void onInvite(String chan, IRCUser u, String nickPass)
	{
		System.out.println(chan + "> " + u.getNick() + " invites " + nickPass);
	}

	public void onJoin(String chan, IRCUser u)
	{
		System.out.println(chan + "> " + u.getNick() + " joins");
	}

	public void onKick(String chan, IRCUser u, String nickPass, String msg)
	{
		System.out.println(chan + "> " + u.getNick() + " kicks " + nickPass);
	}

	public void onMode(IRCUser u, String nickPass, String mode)
	{
		System.out.println("Mode: " + u.getNick() + " sets modes " + mode + " " + nickPass);
	}

	public void onMode(String chan, IRCUser u, IRCModeParser mp) 
	{
		System.out.println(chan + "> " + u.getNick() + " sets mode: " + mp.getLine());
	}

	public void onNick(IRCUser u, String nickNew) 
	{
		System.out.println("Nick: " + u.getNick() + " is now known as " + nickNew);
	}

	public void onNotice(String target, IRCUser u, String msg) 
	{
		System.out.println(target + "> " + u.getNick() + " (notice): " + msg);
	}

	public void onPart(String chan, IRCUser u, String msg) 
	{
		System.out.println(chan + "> " + u.getNick() + " (part): " + msg);
	}

	public void onPrivmsg(String chan, IRCUser u, String msg) 
	{
		System.out.println(chan + "> " + u.getNick() + ": " + msg);
	}

	public void onQuit(IRCUser u, String msg) 
	{
		System.out.println("Quit: " + u.getNick());
	}

	public void onReply(int num, String value, String msg) 
	{
		System.out.println("Reply #" + num + ": " + value + " " + msg);
	}

	public void onTopic(String chan, IRCUser u, String topic) 
	{
		System.out.println(chan + "> " + u.getNick() + " changes topic into: " + topic);
	}

	public void onPing(String p) 
	{
	}

	public void unknown(String a, String b, String c, String d) 
	{
		System.out.println("UNKNOWN: " + a + " b " + c + " " + d);
	}
}
