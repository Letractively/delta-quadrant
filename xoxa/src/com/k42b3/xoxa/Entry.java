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

public class Entry 
{
	public static void main(String[] args)
	{
		Xoxa instance = new Xoxa();

		File config;

		if(args.length > 0)
		{
			config = new File(args[0]);
		}
		else
		{
			config = new File("xoxa.conf.xml");
		}

		if(config.isFile())
		{
			instance.parseConfig(config);
		}
		else
		{
			System.err.println("Could not find config file");
		}
	}
}
