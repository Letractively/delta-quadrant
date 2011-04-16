/**
 * Kadabra
 * 
 * Kadabra is an application to mirror a local folder to an FTP server.
 * You can create multiple projects wich are stored in an SQLite database.
 * With the option --status [id] you can see wich changes are made and
 * with --release [id] you can upload the changes to the FTP server.
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
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
			//Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.OFF);
			//Logger.getLogger("com.k42b3.kadabra").setLevel(Level.OFF);


			// start kadabra
			Kadabra instance = new Kadabra();

			if(args.length == 2 && args[0].equals("--status"))
			{
				int id = Integer.parseInt(args[1]);

				instance.status(id);
			}
			else if(args.length == 2 && args[0].equals("--release"))
			{
				int id = Integer.parseInt(args[1]);

				instance.release(id);
			}
			else if(args.length == 1 && args[0].equals("--list"))
			{
				instance.listProject();
			}
			else if(args.length == 1 && args[0].equals("--add"))
			{
				Console console = System.console();

				String host = console.readLine("Host: ");
				int port = Integer.parseInt(console.readLine("Port: "));
				String user = console.readLine("User: ");
				String pw = new String(console.readPassword("Password: "));
				String localPath = console.readLine("Local path: ");
				String remotePath = console.readLine("Remote path: ");

				Project project = new Project(host, port, user, pw, localPath, remotePath);

				instance.addProject(project);
			}
			else if(args.length == 2 && args[0].equals("--del"))
			{
				int id = Integer.parseInt(args[1]);

				instance.deleteProject(id);
			}
			else if(args.length == 1 && args[0].equals("--build"))
			{
				instance.build();
			}
			else if(args.length == 1 && args[0].equals("--about"))
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
			System.err.println(e.getMessage());
		}
	}
}
