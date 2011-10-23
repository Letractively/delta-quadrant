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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.k42b3.kadabra.HandlerAbstract;
import com.k42b3.kadabra.Item;
import com.k42b3.kadabra.Resource;

/**
 * System
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class System extends HandlerAbstract
{
	public System(Resource resource, String basePath) 
	{
		super(resource, basePath);
	}

	public byte[] getContent(String path) throws Exception
	{
		logger.info(basePath + "/" + path);
		
		InputStream fis = new FileInputStream(basePath + "/" + path);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buf = new byte[1024];
		int len;

		len = fis.read(buf);

		while(len != -1)
		{
			baos.write(buf, 0, len);

			len = fis.read(buf);
		}

		baos.flush();
		baos.close();

		return baos.toByteArray();
	}

	public Item[] getFiles(String path) throws Exception
	{
		logger.info(basePath + "/" + path);

		File list = new File(basePath + "/" + path);

		if(!list.isDirectory())
		{
			throw new Exception(basePath + "/" + path + " is not a directory");
		}

		File[] files = list.listFiles();
		Item[] items = new Item[files.length];

		for(int i = 0; i < files.length; i++)
		{
			String itemName = files[i].getName();
			int itemIype = files[i].isDirectory() ? Item.DIRECTORY : Item.FILE;

			items[i] = new Item(itemName, itemIype);
		}

		logger.info("Found " + items.length + " files");

		return items;
	}

	public void makeDirectory(String path)
	{
		logger.info(basePath + "/" + path);
		
		new File(basePath + "/" + path).mkdir();
	}

	public void uploadFile(String path, byte[] content) throws Exception
	{
		logger.info(basePath + "/" + path);

		FileOutputStream fos = new FileOutputStream(basePath + "/" + path);

		if(fos != null)
		{
			fos.write(content);

			fos.flush();
			fos.close();
		}
	}

	public void close() throws Exception 
	{
	}
	
	public static ArrayList<String> getConfigFields()
	{
		ArrayList<String> fields = new ArrayList<String>();

		return fields;
	}
}
