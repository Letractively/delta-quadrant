/**
 * Kadabra
 * 
 * Kadabra is an application to mirror a left folder to a right folder.
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

package com.k42b3.kadabra.handler;

import java.util.ArrayList;

import com.k42b3.kadabra.HandlerAbstract;
import com.k42b3.kadabra.Item;
import com.k42b3.kadabra.Resource;

/**
 * Ssh
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Ssh extends HandlerAbstract
{
	public Ssh(Resource resource, String basePath) 
	{
		super(resource, basePath);
	}

	public byte[] getContent(String path) throws Exception 
	{
		return null;
	}

	public Item[] getFiles(String path) throws Exception 
	{
		return null;
	}

	public void makeDirecoty(String path) throws Exception 
	{
	}

	public void uploadFile(String path, byte[] content) throws Exception 
	{
	}

	public void close() throws Exception 
	{
	}
	
	public static ArrayList<String> getConfigFields()
	{
		ArrayList<String> fields = new ArrayList<String>();

		fields.add("host");
		fields.add("port");
		fields.add("user");
		fields.add("pw");

		return fields;
	}
}
