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

import java.util.ArrayList;
import java.util.List;

import org.schwering.irc.lib.IRCUser;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * TwitterBot
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class TwitterBot extends BotAbstract
{
	protected Twitter twitter;

	public TwitterBot(String host, int port, String nick, String pass, String channel, boolean ssl, int minInterval, int maxInterval, String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret)
	{
		super(host, port, nick, pass, channel, ssl, minInterval, maxInterval);

		try
		{
			ConfigurationBuilder cb = new ConfigurationBuilder();

			cb.setDebugEnabled(true)
				.setOAuthConsumerKey(consumerKey)
				.setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(accessToken)
				.setOAuthAccessTokenSecret(accessTokenSecret);

			TwitterFactory tf = new TwitterFactory(cb.build());

			this.twitter = tf.getInstance();

			User user = this.twitter.verifyCredentials();

			logger.info("Connected to twitter account " + user.getName());
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
			List<Status> statuses = this.twitter.getFriendsTimeline();
			ArrayList<Resource> resources = new ArrayList<Resource>(limit);

			for(int i = 0; i < statuses.size() && resources.size() < limit; i++)
			{
				Status status = statuses.get(i);

				if(status.getCreatedAt().after(this.getLastUpdated()))
				{
					Resource res = new Resource();
					res.setId("" + status.getId());
					res.setTitle(status.getText());
					res.setLink(status.getSource());
					res.setDate(status.getCreatedAt());

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

	public void onPart(String chan, IRCUser u, String msg)
	{
		super.onPart(chan, u, msg);

		if(chan.equals(this.nick))
		{
			try
			{
				Status status = twitter.updateStatus(msg);

				logger.info("Update status " + status.getId());
			}
			catch(Exception e)
			{
				conn.doPrivmsg(u.getNick(), e.getMessage());

				logger.warning(e.getMessage());
			}
		}
	}
}
