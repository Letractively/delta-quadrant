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

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

public class GmailBot extends BotAbstract
{
	protected Store store;

	public GmailBot(String host, int port, String nick, String pass, String channel, boolean ssl, int minInterval, int maxInterval, String username, String password)
	{
		super(host, port, nick, pass, channel, ssl, minInterval, maxInterval);

		try
		{
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");

			Session session = Session.getDefaultInstance(props, null);
			this.store = session.getStore("imaps");
			store.connect("imap.gmail.com", username, password);

			if(store.isConnected())
			{
				logger.info("Connected to " + host);
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

	public ArrayList<Resource> getResources(int limit) 
	{
		try 
		{
			ArrayList<Resource> resources = new ArrayList<Resource>(limit);

			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
			Folder inbox = store.getFolder("Inbox");
			inbox.open(Folder.READ_ONLY);
			Message messages[] = inbox.search(ft);

			for(int i = 0; i < messages.length && resources.size() < limit; i++)
			{
				Message msg = messages[i];

				if(msg.getReceivedDate().after(this.getLastUpdated()))
				{
					Resource res = new Resource();
					res.setId("" + msg.getMessageNumber());
					res.setTitle(msg.getSubject());
					res.setLink("#");
					res.setDate(msg.getReceivedDate());

					resources.add(res);
				}
			}

			return resources;
		} 
		catch(Exception e) 
		{
			logger.warning(e.getMessage());
			
			return null;
		}
	}
}
