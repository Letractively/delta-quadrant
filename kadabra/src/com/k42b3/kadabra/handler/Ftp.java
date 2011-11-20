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

package com.k42b3.kadabra.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.k42b3.kadabra.HandlerAbstract;
import com.k42b3.kadabra.Item;
import com.k42b3.kadabra.Kadabra;
import com.k42b3.kadabra.Resource;

/**
 * Ftp
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Ftp extends HandlerAbstract
{
	private FTPClient client;

	public Ftp(Resource resource, String basePath) throws Exception
	{
		super(resource, basePath);

		client = new FTPClient();
		client.addProtocolCommandListener(new CommandLogger());
		client.connect(resource.getString("host"), Integer.parseInt(resource.getString("port")));
		client.login(resource.getString("user"), resource.getString("pw"));
		client.setFileType(FTPClient.BINARY_FILE_TYPE);
		client.enterLocalPassiveMode();
	}

	public byte[] getContent(String path) throws Exception
	{
		logger.info(basePath + "/" + path);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		client.retrieveFile(basePath + "/" + path, baos);

		baos.flush();
		baos.close();

		return baos.toByteArray();
	}

	public Item[] getFiles(String path) throws Exception
	{
		path = path.isEmpty() ? "." : path;

		logger.info(basePath + "/" + path);

		FTPFile[] files = client.listFiles(basePath + "/" + path);

		if(client.getReplyCode() != FTPReply.CODE_226)
		{
			throw new Exception(basePath + "/" + path + " ist not a directory");
		}

		Item[] items = new Item[files.length];

		for(int i = 0; i < files.length; i++)
		{
			if(files[i].isDirectory())
			{
				items[i] = new Item(files[i].getName(), Item.DIRECTORY);
			}

			if(files[i].isFile())
			{
				byte[] content = this.getContent(path + "/" + files[i].getName());
				String md5 = DigestUtils.md5Hex(Kadabra.normalizeContent(content));

				items[i] = new Item(files[i].getName(), Item.FILE, md5);
			}
		}

		logger.info("Found " + items.length + " files");

		return items;
	}

	public void makeDirectory(String path) throws Exception
	{
		logger.info(basePath + "/" + path);

		client.makeDirectory(basePath + "/" + path);
	}

	public void uploadFile(String path, byte[] content) throws Exception
	{
		logger.info(basePath + "/" + path);

		OutputStream os = client.storeFileStream(path);

		if(os != null)
		{
			os.write(content);

			os.flush();
			os.close();

			client.completePendingCommand();
		}
	}

	public void close() throws Exception
	{
		client.disconnect();
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

	public class CommandLogger implements ProtocolCommandListener
	{
		private Logger logger;

		public CommandLogger()
		{
			logger = Logger.getLogger("com.k42b3.kadabra");
		}

		public void protocolCommandSent(ProtocolCommandEvent e) 
		{
			logger.info(e.getMessage().trim());
		}

		public void protocolReplyReceived(ProtocolCommandEvent e) 
		{
			logger.info(e.getMessage().trim());
		}
	}
}
