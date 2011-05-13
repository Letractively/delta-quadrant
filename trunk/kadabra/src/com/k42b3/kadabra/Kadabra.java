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
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

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
	public static String version = "0.0.2 beta";

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
			Project project = new Project(db, projectId);

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
			Project project = new Project(db, projectId);

			this.mirrorFolder(project, false, "");

			project.addRelease();

			project.close();

			console.printf("Project release successful%n");
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
			String sql = "SELECT " +
				"id, " +
				"name, " +
				"leftPath, " +
				"leftResourceId, " +
				"resourcesLeft.type " +
				"rightPath, " +
				"rightResourceId, " +
				"resourcesRight.type, " +
				"date, " +
				"(SELECT COUNT(id) FROM releases WHERE releases.projectId = projects.id) AS count " +
			"FROM " +
				"projects " +
			"INNER JOIN " +
				"resources `resourcesLeft` " +
			"ON " +
				"projects.leftResourceId = resources.id " +
			"INNER JOIN " +
				"resources `resourcesRight`" +
			"ON " +
				"projects.rightResourceId = resources.id " +
			"ORDER BY " +
				"name ASC";

			SQLiteStatement st = db.prepare(sql);

			String formatString = "%1$-4s %2$-16s %3$-32s (%3$-8s) %4$-32s (%3$-8s) %5$-6s%n";

			console.printf(formatString, "Id", "Name", "leftPath", "leftType", "rightPath", "rightType", "Releases");

			while(st.step())
			{
				console.printf(formatString, st.columnString(0), st.columnString(1), st.columnString(3), st.columnString(5), st.columnString(6), st.columnString(8), st.columnString(10));
			}
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public void infoProject(int projectId)
	{
		try
		{
			Project project = new Project(db, projectId);

			String sql = "SELECT " +
				"id, " +
				"pattern " +
			"FROM " +
				"exclude" +
			"WHERE " +
				"projectId = " + project.getId();

			SQLiteStatement st = db.prepare(sql);

			console.printf("Left path: " + project.getLeftPath() + "%n");
			console.printf("Right path: " + project.getRightPath() + "%n");

			String formatString = "%1$-4s %2$-16s%n";

			console.printf(formatString, "Id", "Pattern");

			while(st.step())
			{
				console.printf(formatString, st.columnString(0), st.columnString(1));
			}
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public void addProject(String name, String leftPath, int leftResourceId, String rightPath, int rightResourceId)
	{
		try
		{
			Resource leftResource = this.getResource(leftResourceId);
			Resource rightResource = this.getResource(rightResourceId);

			if(leftResource == null)
			{
				throw new Exception("Invalid left resource id");
			}

			if(rightResource == null)
			{
				throw new Exception("Invalid right resource id");
			}

			String sql = "INSERT INTO projects (" +
				"name, " +
				"leftPath, " +
				"leftResourceId, " +
				"rightPath, " +
				"rightResourceId, " +
				"date" +
			") VALUES (" +
				"?, " +
				"?, " +
				"?, " +
				"?, " +
				"?, " +
				"datetime()" +
			")";

			SQLiteStatement st = db.prepare(sql);

			st.bind(1, name);
			st.bind(2, leftPath);
			st.bind(3, leftResourceId);
			st.bind(4, rightPath);
			st.bind(5, rightResourceId);

			st.step();

			console.printf("Add project successful%n");
		}
		catch(Exception e)
		{
			console.printf(e.getMessage() + "%n");

			logger.warning(e.getMessage());
		}
	}

	public void addResource(String type, HashMap<String, String> config)
	{
		try
		{
			// insert record
			String sql = "INSERT INTO resources (" +
				"type, " +
				"config " +
			") VALUES (" +
				"?, " +
				"?" +
			")";

			SQLiteStatement st = db.prepare(sql);

			st.bind(1, type);
			st.bind(2, config.toString());

			st.step();

			console.printf("Add exclude successful%n");
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public void addExclude(int projectId, String pattern)
	{
		try
		{
			Project project = new Project(db, projectId);


			// check pattern
			"foobar".matches(pattern);


			// insert record
			String sql = "INSERT INTO exclude (" +
				"projectId, " +
				"pattern " +
			") VALUES (" +
				"?, " +
				"?" +
			")";

			SQLiteStatement st = db.prepare(sql);

			st.bind(1, project.getId());
			st.bind(2, pattern);

			st.step();

			console.printf("Add exclude successful%n");
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
			Project project = new Project(db, projectId);
			String sql;


			// delete project
			sql = "DELETE FROM " +
				"projects " +
			"WHERE " +
				"id = " + project.getId();

			db.exec(sql); 


			// delete releases
			sql = "DELETE FROM " +
				"releases " +
			"WHERE " +
				"projectId = " + project.getId();

			db.exec(sql); 


			// delete exclude
			sql = "DELETE FROM " +
				"exclude " +
			"WHERE " +
				"projectId = " + project.getId();

			db.exec(sql); 


			console.printf("Delete project " + projectId + " successful%n");
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public void deleteResource(int resourceId)
	{
		try
		{
			// delete exclude
			String sql = "DELETE FROM " +
				"resources " +
			"WHERE " +
				"id = " + resourceId;

			db.exec(sql); 


			console.printf("Delete resource " + resourceId + " successful%n");
		}
		catch(Exception e)
		{
			logger.warning(e.getMessage());
		}
	}

	public void deleteExclude(int excludeId)
	{
		try
		{
			// delete exclude
			String sql = "DELETE FROM " +
				"exclude " +
			"WHERE " +
				"projectId = " + excludeId;

			db.exec(sql); 


			console.printf("Delete exclude " + excludeId + " successful%n");
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
			// projects
			String sql = "CREATE TABLE IF NOT EXISTS projects (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name VARCHAR(128), " +
				"leftPath VARCHAR(512)," +
				"leftResourceId INTEGER," +
				"rightPath VARCHAR(512)," +
				"rightResourceId INTEGER," +
				"date DATETIME" +
			")";

			db.exec(sql);

			// resources
			sql = "CREATE TABLE IF NOT EXISTS resources (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"type ENUM('SYSTEM','FTP','SSH')," +
				"config TEXT" +
			")";

			db.exec(sql);

			// releases
			sql = "CREATE TABLE IF NOT EXISTS releases (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"projectId INTEGER," +
				"date DATETIME" +
			")";

			db.exec(sql);

			// exclude
			sql = "CREATE TABLE IF NOT EXISTS exclude (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"projectId INTEGER," +
				"pattern VARCHAR," +
				"date DATETIME" +
			")";

			db.exec(sql);

			console.printf("Building tables successful%n");
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
		console.printf(formatString, "--status [projectId]", "Shows wich files will be updated but without making any actions");
		console.printf(formatString, "--release [projectId]", "Mirros the local folder to the remote folder");
		console.printf(formatString, "--list", "List all available projects with their project id");
		console.printf(formatString, "--info [projectId]", "Get informations about a project");
		console.printf(formatString, "--add", "Add a new project to the database");
		console.printf(formatString, "--addExclude", "Add a new exclude regexp to an project");
		console.printf(formatString, "--del [projectId]", "Delete a project");
		console.printf(formatString, "--build", "Build the tables in the database if it not exists");
		console.printf(formatString, "--about", "Shows informations about the application");
	}

	private void mirrorFolder(Project project, boolean testRun, String path)
	{
		try
		{
			// get files
			Item[] leftFiles = project.getLeftHandler().getFiles(path);
			Item[] rightFiles = project.getRightHandler().getFiles(path);

			// compare
			for(int i = 0; i < leftFiles.length; i++)
			{
				Item leftItem = leftFiles[i];

				// check whether not current or up dir
				if(leftItem.getName().equals(".") || leftItem.getName().equals(".."))
				{
					continue;
				}

				// check exclude
				ArrayList<String> exclude = project.getExclude();

				for(int j = 0; j < exclude.size(); j++)
				{
					if(leftItem.getName().matches(exclude.get(i)))
					{
						continue;
					}
				}

				// is directory
				if(leftItem.isDirectory())
				{
					if(this.hasFolder(leftItem, rightFiles))
					{
						this.mirrorFolder(project, testRun, path + "/" + leftItem.getName());
					}
					else
					{
						console.printf("A " + path + "/" + leftItem.getName() + "%n");

						if(!testRun)
						{
							project.getRightHandler().makeDirecoty(path + "/" + leftItem.getName());
						}

						this.uploadDir(project, testRun, path + "/" + leftItem.getName());
					}
				}

				if(leftItem.isFile())
				{
					if(this.hasFile(leftItem, rightFiles))
					{
						// compare content
						byte[] leftContent = project.getLeftHandler().getContent(path + "/" + leftItem.getName());
						byte[] rightContent = project.getRightHandler().getContent(path + "/" + leftItem.getName());

						if(!Arrays.equals(leftContent, rightContent))
						{
							int leftLen = leftContent.length;
							int rightLen = rightContent.length;

							console.printf("U " + path + "/" + leftItem.getName() + " (" + leftLen + "/" + rightLen + ")%n");

							if(!testRun)
							{
								project.getRightHandler().uploadFile(path + "/" + leftItem.getName(), leftContent);
							}
						}
					}
					else
					{
						console.printf("A " + path + "/" + leftItem.getName() + "%n");

						if(!testRun)
						{
							byte[] leftContent = project.getLeftHandler().getContent(path + "/" + leftItem.getName());

							project.getRightHandler().uploadFile(path + "/" + leftItem.getName(), leftContent);
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

	private boolean hasFolder(Item file, Item[] files)
	{
		for(int i = 0; i < files.length; i++)
		{
			if(files[i].isDirectory() && files[i].getName().equals(file.getName()))
			{
				return true;
			}
		}

		return false;
	}

	private boolean hasFile(Item file, Item[] files)
	{
		for(int i = 0; i < files.length; i++)
		{
			if(files[i].isFile() && files[i].getName().equals(file.getName()))
			{
				return true;
			}
		}

		return false;
	}

	private void uploadDir(Project project, boolean testRun, String path) throws Exception
	{
		// get left files
		Item[] leftFiles = project.getLeftHandler().getFiles(path);


		// upload
		for(int i = 0; i < leftFiles.length; i++)
		{
			Item leftItem = leftFiles[i];

			// check whether not current or up dir
			if(leftItem.getName().equals(".") || leftItem.getName().equals(".."))
			{
				continue;
			}

			// check exclude
			ArrayList<String> exclude = project.getExclude();

			for(int j = 0; j < exclude.size(); j++)
			{
				if(leftItem.getName().matches(exclude.get(i)))
				{
					continue;
				}
			}

			if(leftItem.isDirectory())
			{
				console.printf("A " + path + "/" + leftItem.getName() + "%n");

				if(!testRun)
				{
					project.getRightHandler().makeDirecoty(path + "/" + leftItem.getName());
				}

				this.uploadDir(project, testRun, path + "/" + leftItem.getName());
			}

			if(leftItem.isFile())
			{
				console.printf("A " + path + "/" + leftItem.getName() + "%n");

				if(!testRun)
				{
					byte[] content = project.getLeftHandler().getContent(path + "/" + leftItem.getName());
					
					project.getRightHandler().uploadFile(path + "/" + leftItem.getName(), content);
				}
			}
		}
	}

	private Resource getResource(int resourceId)
	{
		String sql = "SELECT" +
			"id " +
		"FROM " +
			"resources " +
		"WHERE " +
			"id = " + resourceId;
		
		return null;
	}
}
