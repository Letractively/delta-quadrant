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

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteStatement;

/**
 * Kadabra
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Kadabra
{
	public static String version = "0.0.1 beta";

	private Console console;
	private Logger logger;
	private SQLiteConnection db;

	public Kadabra()
	{
		this.console = System.console();
		this.logger = Logger.getLogger("com.k42b3.kadabra");
		
		try
		{
			db = new SQLiteConnection(new File("projects"));
			db.open(true);
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public void status(int projectId)
	{
		try
		{
			Project project = this.getProject(projectId);

			this.mirrorFolder(project, true, "");
			
			project.close();
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public void release(int projectId)
	{
		try
		{
			Project project = this.getProject(projectId);

			this.mirrorFolder(project, false, "");
			
			project.close();
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public void listProject()
	{
		try
		{
			SQLiteStatement st = db.prepare("SELECT id, host, port, user, pw, localPath, remotePath FROM projects");

			String formatString = "%1$-4s %2$-16s %3$-32s %4$-32s%n";

			console.printf(formatString, "Id", "Host", "LocalPath", "RemotePath");

			while(st.step())
			{
				console.printf(formatString, st.columnString(0), st.columnString(1), st.columnString(5), st.columnString(6));
			}
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public void addProject(Project project)
	{
		try
		{
			String sql = "INSERT INTO projects (host, port, user, pw, localPath, remotePath) VALUES (?, ?, ?, ?, ?, ?)";

			SQLiteStatement st = db.prepare(sql);

			st.bind(1, project.getHost());
			st.bind(2, project.getPort());
			st.bind(3, project.getUser());
			st.bind(4, project.getPw());
			st.bind(5, project.getLocalPath());
			st.bind(6, project.getRemotePath());

			st.step();

			console.printf("Add project successful%n");
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public void deleteProject(int projectId)
	{
		try
		{
			String sql = "DELETE FROM projects WHERE id = " + projectId;

			db.exec(sql); 

			console.printf("Delete project " + projectId + " successful%n");
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public void build()
	{
		try
		{
			String sql = "CREATE TABLE IF NOT EXISTS projects (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"host VARCHAR(128)," +
				"port INTEGER," +
				"user VARCHAR(128)," +
				"pw VARCHAR(128)," +
				"localPath VARCHAR(256)," +
				"remotePath VARCHAR(256)" +
				")";

			db.exec(sql); 

			console.printf("Building table successful%n");
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public void about()
	{
		String formatString = "%1$-12s %2$-64s%n";

		console.printf("Kadabra version " + version + "%n");
		console.printf(formatString, "Author", "Christoph \"k42b3\" Kappestein");
		console.printf(formatString, "Website", "http://code.google.com/p/delta-quadrant");
		console.printf(formatString, "License", "GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>");
		console.printf("%n");
		console.printf("Kadabra is an application to mirror a local folder to an FTP server.%n");
		console.printf("You can create multiple projects wich are stored in an SQLite database.%n");
		console.printf("With the option --status [id] you can see wich changes are made and%n");
		console.printf("with --release [id] you can upload the changes to the FTP server.%n");
	}

	public void help()
	{
		String formatString = "%1$-32s %2$-64s%n";

		console.printf(formatString, "Argument", "Description");
		console.printf(formatString, "--status [projectId]", "Shows wich files will be updated but without making any action");
		console.printf(formatString, "--release [projectId]", "Mirros the local folder to the remote folder");
		console.printf(formatString, "--list", "List all available projects with theri project id");
		console.printf(formatString, "--add", "Add a new project to the database");
		console.printf(formatString, "--del [projectId]", "Delete a project");
		console.printf(formatString, "--build", "Build the project table in the database if it not exists");
		console.printf(formatString, "--about", "Shows informations about the application");
	}

	private Project getProject(int projectId) throws Exception
	{
		SQLiteStatement st = db.prepare("SELECT host, port, user, pw, localPath, remotePath FROM projects WHERE id = ?");
		st.bind(1, projectId);

		st.step();

		if(st.hasRow())
		{
			String host = st.columnString(0);
			int port = st.columnInt(1);
			String user = st.columnString(2);
			String pw = st.columnString(3);
			String localPath = st.columnString(4);
			String remotePath = st.columnString(5);

			Project project = new Project(host, port, user, pw, localPath, remotePath);

			return project;
		}
		else
		{
			throw new Exception("Invalid project id");
		}
	}

	private File[] getLocalFiles(Project project, String path) throws Exception
	{
		File list = new File(project.getLocalPath() + "/" + path);

		if(!list.isDirectory())
		{
			throw new Exception(project.getLocalPath() + "/" + path + " is not a directory");
		}

		return list.listFiles();
	}

	private FTPFile[] getRemoteFiles(Project project, String path) throws Exception
	{
		FTPFile[] remoteFiles = project.getClient().listFiles(project.getRemotePath() + "/" + path);

		if(project.getClient().getReplyCode() != FTPReply.CODE_226)
		{
			throw new Exception(path + " ist not a directory");
		}
		
		return remoteFiles;
	}

	private void mirrorFolder(Project project, boolean testRun, String path)
	{
		try
		{
			// get local files
			File[] localFiles = this.getLocalFiles(project, path);

			// get remote files
			FTPFile[] remoteFiles = this.getRemoteFiles(project, path);

			// compare
			for(int i = 0; i < localFiles.length; i++)
			{
				File localItem = localFiles[i];
				String localPath;

				if(path.isEmpty())
				{
					localPath = localItem.getName();
				}
				else
				{
					localPath = path + "/" + localItem.getName();
				}

				if(localItem.getName().charAt(0) == '.')
				{
					continue;
				}

				if(localItem.isDirectory())
				{
					if(this.hasFolder(localItem, remoteFiles))
					{
						this.mirrorFolder(project, testRun, localPath);
					}
					else
					{
						console.printf("A " + project.getRemotePath() + "/" + localPath + "%n");

						if(!testRun)
						{
							project.getClient().makeDirectory(project.getRemotePath() + "/" + localPath);
						}

						this.uploadDir(project, testRun, localPath);
					}
				}

				if(localItem.isFile())
				{
					if(this.hasFile(localItem, remoteFiles))
					{
						// get local content
						ByteArrayOutputStream baosLocal = new ByteArrayOutputStream();
						FileInputStream is = new FileInputStream(localItem);

						int len;
						byte[] tmp = new byte[1024];

						while((len = is.read(tmp)) != -1)
						{
							baosLocal.write(tmp, 0, len);
						}

						baosLocal.flush();
						baosLocal.close();


						// get remote content
						ByteArrayOutputStream baosRemote = new ByteArrayOutputStream();

						project.getClient().retrieveFile(project.getRemotePath() + "/" + localPath, baosRemote);

						baosRemote.flush();
						baosRemote.close();


						// compare content
						if(!Arrays.equals(baosLocal.toByteArray(), baosRemote.toByteArray()))
						{
							int localLen = baosLocal.toByteArray().length;
							int remoteLen = baosRemote.toByteArray().length;

							console.printf("U " + project.getRemotePath() + "/" + localPath + " (" + localLen + "/" + remoteLen + ")%n");

							if(!testRun)
							{
								this.uploadFile(project.getClient(), localItem, project.getRemotePath() + "/" + localPath);
							}
						}
					}
					else
					{
						console.printf("A " + project.getRemotePath() + "/" + localPath + "%n");

						if(!testRun)
						{
							this.uploadFile(project.getClient(), localItem, project.getRemotePath() + "/" + localPath);
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	private void uploadFile(FTPClient client, File localItem, String dest) throws Exception
	{
		InputStream fis = new FileInputStream(localItem);

		if(!client.storeFile(dest, fis))
		{
			throw new Exception("Could not upload file");
		}
	}

	private boolean hasFolder(File localItem, FTPFile[] remoteFiles)
	{
		for(int i = 0; i < remoteFiles.length; i++)
		{
			if(remoteFiles[i].isDirectory() && remoteFiles[i].getName().equals(localItem.getName()))
			{
				return true;
			}
		}

		return false;
	}

	private boolean hasFile(File localItem, FTPFile[] remoteFiles)
	{
		for(int i = 0; i < remoteFiles.length; i++)
		{
			if(remoteFiles[i].isFile() && remoteFiles[i].getName().equals(localItem.getName()))
			{
				return true;
			}
		}

		return false;
	}

	private void uploadDir(Project project, boolean testRun, String path) throws Exception
	{
		// get local files
		File[] localFiles = this.getLocalFiles(project, path);

		// get remote files
		FTPFile[] remoteFiles = this.getRemoteFiles(project, path);

		// upload
		for(int i = 0; i < localFiles.length; i++)
		{
			File localItem = localFiles[i];
			String localPath;

			if(path.isEmpty())
			{
				localPath = localItem.getName();
			}
			else
			{
				localPath = path + "/" + localItem.getName();
			}
			
			if(localItem.getName().charAt(0) == '.')
			{
				continue;
			}

			if(localItem.isDirectory())
			{
				console.printf("A " + project.getRemotePath() + "/" + localPath + "%n");

				if(!testRun)
				{
					project.getClient().makeDirectory(project.getRemotePath() + "/" + localPath);
				}

				this.uploadDir(project, testRun, localPath);
			}

			if(localItem.isFile())
			{
				console.printf("A " + project.getRemotePath() + "/" + localPath + "%n");

				if(!testRun)
				{
					this.uploadFile(project.getClient(), localItem, project.getRemotePath() + "/" + localPath);
				}
			}
		}
	}
}
