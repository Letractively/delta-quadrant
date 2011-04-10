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

import java.io.File;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class Xoxa
{
	public final static String version = "0.0.2";

	private Logger logger;

	public Xoxa()
	{
		logger = Logger.getLogger("com.k42b3.xoxa");
	}

	public void parseConfig(File config)
	{
		try
		{
			logger.info("Parse config " + config.getPath());

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(config);

			Element rootElement = (Element) doc.getDocumentElement();

			rootElement.normalize();

			if(rootElement.hasAttribute("host") && 
				rootElement.hasAttribute("port") && 
				rootElement.hasAttribute("channel") && 
				rootElement.hasAttribute("ssl"))
			{
				String host = rootElement.getAttribute("host");
				int port = Integer.parseInt(rootElement.getAttribute("port"));
				String channel = rootElement.getAttribute("channel");
				boolean ssl = Boolean.parseBoolean(rootElement.getAttribute("ssl"));

				NodeList botList = doc.getElementsByTagName("bot");

				for(int i = 0; i < botList.getLength(); i++) 
				{
					Node botNode = botList.item(i);
					Element botElement = (Element) botNode;

					try
					{
						BotAbstract bot = BotFactory.getInstance(host, port, channel, ssl, botElement);

						if(bot != null)
						{
							logger.info("Create bot instance " + botElement.getAttribute("nick") + " successful");

							// wait betwen 2 and 4 minutes to join the next bot
							int min = 60000 * 2;
							int max = 60000 * 4;
							int wait = min + (int) (Math.random() * (max - min) + 0.5);

							Thread.sleep(wait);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						logger.warning(e.getMessage());
					}
				}
			}
			else
			{
				logger.warning("Required attribute is missing in configuration xml");
			}
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}
}

