/**
 * Kadabra
 * 
 * Kadabra is an application to mirror a source folder to a destination folder.
 * You can create multiple projects wich are stored in an SQLite database.
 * With the option --status [id] you can see wich changes are made and
 * with --release [id] you update the changes. You can use different handler
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

import java.util.ArrayList;

import com.k42b3.kadabra.record.Resource;

/**
 * HandlerFactory
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class HandlerFactory 
{
	public static HandlerAbstract factory(Resource resource, String path) throws Exception
	{
		if(resource.getType().equals("SYSTEM"))
		{
			return new com.k42b3.kadabra.handler.System(resource, path);
		}
		else if(resource.getType().equals("FTP"))
		{
			return new com.k42b3.kadabra.handler.Ftp(resource, path);
		}
		else if(resource.getType().equals("SSH"))
		{
			return new com.k42b3.kadabra.handler.Ssh(resource, path);
		}
		else
		{
			throw new Exception("Invalid resource type");
		}
	}

	public static ArrayList<String> factoryConfig(String type) throws Exception
	{
		if(type.equals("SYSTEM"))
		{
			return com.k42b3.kadabra.handler.System.getConfigFields();
		}
		else if(type.equals("FTP"))
		{
			return com.k42b3.kadabra.handler.Ftp.getConfigFields();
		}
		else if(type.equals("SSH"))
		{
			return com.k42b3.kadabra.handler.Ssh.getConfigFields();
		}
		else
		{
			throw new Exception("Invalid type");
		}
	}
}
