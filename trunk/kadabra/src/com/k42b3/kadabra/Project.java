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

import java.io.File;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Project
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Project 
{
	private int id;
	private String host;
	private int port;
	private String user;
	private String pw;
	private String localPath;
	private String remotePath;

	private FTPClient client;

	public Project(int id, String host, int port, String user, String pw, String localPath, String remotePath) throws Exception
	{
		this.setId(id);
		this.setHost(host);
		this.setPort(port);
		this.setUser(user);
		this.setPw(pw);
		this.setLocalPath(localPath);
		this.setRemotePath(remotePath);
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getHost() 
	{
		return host;
	}

	public void setHost(String host) throws Exception
	{
		if(host.length() >= 3)
		{
			this.host = host;
		}
		else
		{
			throw new Exception("Host must have at least 3 signs");
		}
	}

	public int getPort() 
	{
		return port;
	}

	public void setPort(int port) throws Exception
	{
		if(port > 0 && port <= 65535)
		{
			this.port = port;
		}
		else
		{
			throw new Exception("Port must be greater then 0 and lower or equal to 65535");
		}
	}

	public String getUser() 
	{
		return user;
	}

	public void setUser(String user) throws Exception
	{
		if(user.length() >= 3)
		{
			this.user = user;
		}
		else
		{
			throw new Exception("User must have at least 3 signs");
		}
	}

	public String getPw() 
	{
		return pw;
	}

	public void setPw(String pw) throws Exception
	{
		if(pw.length() >= 3)
		{
			this.pw = pw;
		}
		else
		{
			throw new Exception("Pw must have at least 3 signs");
		}
	}

	public String getLocalPath() 
	{
		return localPath;
	}

	public void setLocalPath(String localPath) throws Exception
	{
		localPath = localPath.trim();

		File file = new File(localPath);

		if(file.isDirectory())
		{
			this.localPath = file.getAbsolutePath();
		}
		else
		{
			throw new Exception("localPath is not a folder");
		}
	}

	public String getRemotePath() 
	{
		return remotePath;
	}

	public void setRemotePath(String remotePath) throws Exception
	{
		remotePath = remotePath.trim();

		if(remotePath.endsWith("/"))
		{
			remotePath = remotePath.substring(0, remotePath.length() - 1);
		}

		FTPClient client = this.getClient();

		client.listFiles(remotePath);
		
		if(client.getReplyCode() == FTPReply.CODE_226)
		{
			this.remotePath = remotePath;
		}
		else
		{
			throw new Exception("remotePath ist not valid");
		}
	}

	public FTPClient getClient() throws Exception
	{
		if(client == null)
		{
			client = new FTPClient();

			client.addProtocolCommandListener(new CommandLogger());

			client.connect(this.getHost(), this.getPort());

			client.login(this.getUser(), this.getPw());

			client.enterLocalPassiveMode();

			client.setFileType(FTPClient.BINARY_FILE_TYPE);
		}

		return client;
	}
	
	public void close() throws Exception
	{
		client.disconnect();
	}
}
