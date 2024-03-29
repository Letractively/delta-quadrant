/**
 * Kadabra
 * 
 * Kadabra is an application to mirror a source folder to a destination folder.
 * You can create multiple projects wich are stored in an SQLite database.
 * With the command status [id] you can see wich changes are made and
 * with release [id] you update the changes. You can use different handler
 * like System or FTP.
 * 
 * Copyright (c) 2010, 2011 Christoph Kappestein <k42b3.x@gmail.com>
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

package com.k42b3.kadabra;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Entry 
{
	public static void main(String[] args)
	{
		try
		{
			// disable logging
			Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.OFF);
			Logger.getLogger("com.k42b3.kadabra").setLevel(Level.OFF);


			// start kadabra
			Console console = System.console();
			Kadabra instance = new Kadabra();

			if(args.length == 2 && args[0].equals("status"))
			{
				int id = Integer.parseInt(args[1]);

				instance.status(id);
			}
			else if(args.length == 2 && args[0].equals("release"))
			{
				int id = Integer.parseInt(args[1]);

				instance.release(id);
			}
			else if(args.length == 1 && args[0].equals("list"))
			{
				instance.listProject();
			}
			else if(args.length == 2 && args[0].equals("list") && args[1].equals("resource"))
			{
				instance.listResource();
			}
			else if(args.length == 1 && args[0].equals("add"))
			{
				String name = console.readLine("Name: ");

				String leftPath = console.readLine("Left path: ");
				int legtResourceId = Integer.parseInt(console.readLine("Left resource id: "));

				String rightPath = console.readLine("Right path: ");
				int rightResourceId = Integer.parseInt(console.readLine("Right resource id: "));

				instance.addProject(name, leftPath, legtResourceId, rightPath, rightResourceId);
			}
			else if(args.length == 2 && args[0].equals("add") && args[1].equals("resource"))
			{
				String type = console.readLine("Type [System|Ftp|Ssh]: ").toUpperCase();
				HashMap<String, String> config = new HashMap<String, String>();
				ArrayList<String> configFields = HandlerFactory.factoryConfig(type);

				String name = console.readLine("Name: ");

				for(int i = 0; i < configFields.size(); i++)
				{
					String key = configFields.get(i);
					String value = console.readLine(key + ": ");

					config.put(key, value);
				}

				instance.addResource(type, name, config);
			}
			else if(args.length == 2 && args[0].equals("add") && args[1].equals("exclude"))
			{
				int projectId = Integer.parseInt(console.readLine("Project ID: "));
				String pattern = console.readLine("Pattern: ");

				instance.addExclude(projectId, pattern);
			}
			else if(args.length == 1 && args[0].equals("del"))
			{
				int projectId = Integer.parseInt(console.readLine("Project ID: "));

				instance.deleteProject(projectId);
			}
			else if(args.length == 2 && args[0].equals("del") && args[1].equals("resource"))
			{
				int resourceId = Integer.parseInt(console.readLine("Resource ID: "));

				instance.deleteResource(resourceId);
			}
			else if(args.length == 2 && args[0].equals("del") && args[1].equals("exclude"))
			{
				int excludeId = Integer.parseInt(console.readLine("Exclude ID: "));

				instance.deleteExclude(excludeId);
			}
			else if(args.length == 1 && args[0].equals("build"))
			{
				instance.build();
			}
			else if(args.length == 2 && args[0].equals("info"))
			{
				int id = Integer.parseInt(args[1]);

				instance.info(id);
			}
			else if(args.length == 1 && args[0].equals("about"))
			{
				instance.about();
			}
			else
			{
				instance.help();
			}
		}
		catch(Exception e)
		{
			Kadabra.handleException(e);
		}
	}
}
