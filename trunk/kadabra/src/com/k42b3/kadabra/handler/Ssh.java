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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
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
	private Session session;
	private ChannelSftp channel;

	public Ssh(Resource resource, String basePath) throws Exception
	{
		super(resource, basePath);

		JSch jsch = new JSch();

		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");

		session = jsch.getSession(resource.getString("user"), resource.getString("host"), Integer.parseInt(resource.getString("port")));
		session.setConfig(config);
		session.setPassword(resource.getString("pw"));
		session.connect();

		channel = (ChannelSftp) session.openChannel("sftp");
		channel.connect();
	}

	public byte[] getContent(String path) throws Exception 
	{
		logger.info(basePath + "/" + path);

		InputStream is = channel.get(basePath + "/" + path);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buf = new byte[1024];
		int len;

		len = is.read(buf);

		while(len != -1)
		{
			baos.write(buf, 0, len);

			len = is.read(buf);
		}

		baos.flush();
		baos.close();

		return baos.toByteArray();
	}

	public Item[] getFiles(String path) throws Exception 
	{
		logger.info(basePath + "/" + path);

		Vector list = channel.ls(basePath + "/" + path);
		Item[] items = new Item[list.size()];

		for(int i = 0; i < list.size(); i++)
		{
			LsEntry entry = (LsEntry) list.get(i);

			String itemName = entry.getFilename();
			int itemIype = entry.getAttrs().isDir() ? Item.DIRECTORY : Item.FILE;

			items[i] = new Item(itemName, itemIype);
		}

		return items;
	}

	public void makeDirectory(String path) throws Exception 
	{
		logger.info(basePath + "/" + path);

		channel.mkdir(basePath + "/" + path);
	}

	public void uploadFile(String path, byte[] content) throws Exception 
	{
		logger.info(basePath + "/" + path);

		OutputStream os = channel.put(path);

		os.write(content);

		os.close();
	}

	public void close() throws Exception 
	{
		channel.disconnect();
		session.disconnect();
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
