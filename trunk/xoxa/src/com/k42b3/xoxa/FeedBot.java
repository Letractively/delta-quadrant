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

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * FeedBot
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class FeedBot extends BotAbstract
{
	protected ArrayList<String> sources;
	protected SyndFeedInput input;

	public FeedBot(String host, int port, String nick, String pass, String channel, boolean ssl, int minInterval, int maxInterval, ArrayList<String> sources)
	{
		super(host, port, nick, pass, channel, ssl, minInterval, maxInterval);

		this.sources = sources;
		this.input = new SyndFeedInput();
	}

	public ArrayList<Resource> getResources(int limit)
	{
		try
		{
			ArrayList<Resource> resources = new ArrayList<Resource>(limit);

			for(int i = 0; i < this.sources.size(); i++)
			{
				URL url = new URL(this.sources.get(i));
				List<SyndEntry> entries = this.requestFeed(this.sources.get(i));

				if(entries != null)
				{
					for(int j = 0; j < entries.size() && resources.size() < limit; j++)
					{
						SyndEntry entry = entries.get(j);

						if(entry.getPublishedDate().after(this.getLastUpdated()))
						{
							Resource res = new Resource();
							res.setId(entry.getUri());
							res.setTitle(url.getHost() + ": " + entry.getTitle());
							res.setLink(entry.getUri());
							res.setDate(entry.getPublishedDate());

							resources.add(res);
						}
					}
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
	
	public List<SyndEntry> requestFeed(String url)
	{
		// send http message to bot
		try
		{
			// build request
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

			// header
			HttpGet httpGet = new HttpGet(url);

			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");

			httpGet.setHeader("If-Modified-Since", sdf.format(this.getLastUpdated()));

	        // execute HTTP Get Request
			logger.fine("Executing request " + httpGet.getRequestLine());

			HttpResponse httpResponse = httpClient.execute(httpGet);

			logger.fine("Received " + httpResponse.getStatusLine());

			// parse response
			XmlReader reader = new XmlReader(httpResponse.getEntity().getContent());

			SyndFeed feed = this.input.build(reader);

			return feed.getEntries();
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());

			return null;
		}
	}
}
