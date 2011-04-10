/**
 * xoxa
 * 
 * An IRC bot wich you can configure via an XML file. The bot can create 
 * multiple users wich can join a specific channel. The main task of the bot
 * is to deliver real time messages from different sources (feeds, twitter,
 * gmail, etc.)
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BotFactory 
{
	public static BotAbstract getInstance(String host, int port, String channel, boolean ssl, Element bot)
	{
		Logger logger = Logger.getLogger("com.k42b3.xoxa");

		if(bot.hasAttribute("class") && bot.hasAttribute("nick"))
		{
			int minInterval;
			int maxInterval;

			if(bot.getAttribute("minInterval") != null)
			{
				minInterval = Integer.parseInt(bot.getAttribute("minInterval"));
			}
			else
			{
				minInterval = 300;
			}

			if(bot.getAttribute("minInterval") != null)
			{
				maxInterval = Integer.parseInt(bot.getAttribute("maxInterval"));
			}
			else
			{
				maxInterval = 600;
			}

			logger.warning("Set min and max interval to " + minInterval + " / " + maxInterval + " seconds");

			// we get the intervals in seconds but we need milliseconds
			minInterval = minInterval * 1000;
			maxInterval = maxInterval * 1000;


			String cls = bot.getAttribute("class");
			String nick = bot.getAttribute("nick");
			String pass = bot.getAttribute("pass");

			if(cls != null &&
				nick != null &&
				cls.length() > 3 &&
				nick.length() > 3)
			{
				if(cls.equals("FeedBot"))
				{
					return buildFeedBot(host, port, nick, pass, channel, ssl, minInterval, maxInterval, bot);
				}
				else if(cls.equals("TwitterBot"))
				{
					return buildTwitterBot(host, port, nick, pass, channel, ssl, minInterval, maxInterval, bot);
				}
				else if(cls.equals("GmailBot"))
				{
					return buildGmailBot(host, port, nick, pass, channel, ssl, minInterval, maxInterval, bot);
				}
				else if(cls.equals("FacebookBot"))
				{
					return buildFacebookBot(host, port, nick, pass, channel, ssl, minInterval, maxInterval, bot);
				}
				else
				{
					logger.warning("Invalid bot class " + bot.getAttribute("class"));
				}
			}
			else
			{
				logger.warning("Class or nick must be greater then 3 signs for " + bot.getAttribute("nick"));
			}
		}
		else
		{
			logger.warning("Missing bot attribute");
		}

		return null;
	}

	public static BotAbstract buildFeedBot(String host, int port, String nick, String pass, String channel, boolean ssl, int minInterval, int maxInterval, Element bot)
	{
		Logger logger = Logger.getLogger("com.k42b3.xoxa");

		NodeList srcList = bot.getElementsByTagName("src");
		ArrayList<String> sources = new ArrayList<String>();

		for(int i = 0; i < srcList.getLength(); i++) 
		{
			Node srcNode = srcList.item(i);
			String url = srcNode.getTextContent();

			if(url != null && url.length() > 3)
			{
				try
				{
					new URL(url);

					sources.add(url);
				}
				catch(MalformedURLException e)
				{
					logger.info("Skipping malformed URL " + url);
				}
			}
			else
			{
				logger.warning("Value must be greater then 3 signs for " + bot.getAttribute("nick"));
			}
		}

		if(sources.size() > 0)
		{
			logger.info("Found " + sources.size() + " sources for " + bot.getAttribute("nick"));

			return new FeedBot(host, port, nick, pass, channel, ssl, minInterval, maxInterval, sources);
		}
		else
		{
			logger.info("Found no sources for " + bot.getAttribute("nick"));

			return null;
		}
	}

	public static BotAbstract buildTwitterBot(String host, int port, String nick, String pass, String channel, boolean ssl, int minInterval, int maxInterval, Element bot)
	{
		Logger logger = Logger.getLogger("com.k42b3.xoxa");

		Node consumerKeyNode = bot.getElementsByTagName("consumerKey").item(0);
		Node consumerSecretNode = bot.getElementsByTagName("consumerSecret").item(0);
		Node accessTokenNode = bot.getElementsByTagName("accessToken").item(0);
		Node accessTokenSecretNode = bot.getElementsByTagName("accessTokenSecret").item(0);
		
		if(consumerKeyNode != null &&
			consumerSecretNode != null &&
			accessTokenNode != null &&
			accessTokenSecretNode != null)
		{
			String consumerKey = consumerKeyNode.getTextContent();
			String consumerSecret = consumerSecretNode.getTextContent();
			String accessToken = accessTokenNode.getTextContent();
			String accessTokenSecret = accessTokenSecretNode.getTextContent();

			if(consumerKey != null &&
				consumerSecret != null && 
				accessToken != null &&
				accessTokenSecret != null && 
				consumerKey.length() > 3 && 
				consumerSecret.length() > 3 && 
				accessToken.length() > 3 && 
				accessTokenSecret.length() > 3)
			{
				return new TwitterBot(host, port, nick, pass, channel, ssl, minInterval, maxInterval, consumerKey, consumerSecret, accessToken, accessTokenSecret);
			}
			else
			{
				logger.warning("Values must be greater then 3 signs for " + bot.getAttribute("nick"));

				return null;
			}
		}
		else
		{
			logger.warning("Missing credentials for " + bot.getAttribute("nick"));
			
			return null;
		}
	}

	public static BotAbstract buildGmailBot(String host, int port, String nick, String pass, String channel, boolean ssl, int minInterval, int maxInterval, Element bot)
	{
		Logger logger = Logger.getLogger("com.k42b3.xoxa");

		Node usernameNode = bot.getElementsByTagName("username").item(0);
		Node passwordNode = bot.getElementsByTagName("password").item(0);

		if(usernameNode != null &&
			passwordNode != null)
		{
			String username = usernameNode.getTextContent();
			String password = passwordNode.getTextContent();

			if(username != null && 
				password != null && 
				username.length() > 3 && 
				password.length() > 3)
			{
				return new GmailBot(host, port, nick, pass, channel, ssl, minInterval, maxInterval, username, password);
			}
			else
			{
				logger.warning("Values must be greater then 3 signs for " + bot.getAttribute("nick"));

				return null;
			}
		}
		else
		{
			logger.warning("Missing credentials for " + bot.getAttribute("nick"));

			return null;
		}
	}

	public static BotAbstract buildFacebookBot(String host, int port, String nick, String pass, String channel, boolean ssl, int minInterval, int maxInterval, Element bot)
	{
		return null;
	}
}
