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
import java.io.ObjectOutputStream;
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
	public static String version = "0.0.3 beta";

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
			console.printf(e.getMessage() + "%n");

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
			console.printf(e.getMessage() + "%n");

			logger.warning(e.getMessage());
		}
	}

	public void listProject()
	{
		try
		{
			String sql = "SELECT " +
				"projects.id, " +
				"projects.name, " +
				"projects.leftPath, " +
				"projects.leftResourceId, " +
				"resourcesLeft.type, " +
				"projects.rightPath, " +
				"projects.rightResourceId, " +
				"resourcesRight.type, " +
				"projects.date " +
			"FROM " +
				"projects " +
			"INNER JOIN " +
				"resources resourcesLeft " +
			"ON " +
				"projects.leftResourceId = resourcesLeft.id " +
			"INNER JOIN " +
				"resources resourcesRight " +
			"ON " +
				"projects.rightResourceId = resourcesRight.id " +
			"ORDER BY " +
				"projects.name ASC";

			SQLiteStatement st = db.prepare(sql);

			String formatString = "%1$-4s %2$-16s %3$-32s %4$-32s%n";

			console.printf(formatString, "Id", "Name", "leftPath", "rightPath");

			while(st.step())
			{
				console.printf(formatString, 
					st.columnString(0),
					st.columnString(1),
					trimString(st.columnString(2), 32),
					trimString(st.columnString(5), 32));
			}
		}
		catch(Exception e)
		{
			console.printf(e.getMessage() + "%n");

			logger.warning(e.getMessage());
		}
	}

	public void listResource()
	{
		try
		{
			String sql = "SELECT " +
				"id, " +
				"type, " +
				"name " +
			"FROM " +
				"resources " +
			"ORDER BY " +
				"id ASC";

			SQLiteStatement st = db.prepare(sql);

			String formatString = "%1$-4s %2$-8s %3$-32s%n";

			console.printf(formatString, "Id", "Type", "Name");

			while(st.step())
			{
				console.printf(formatString, 
					st.columnString(0),
					st.columnString(1),
					st.columnString(2));
			}
		}
		catch(Exception e)
		{
			console.printf(e.getMessage() + "%n");

			logger.warning(e.getMessage());
		}
	}
	
	public void listExclude()
	{
		try
		{
			String sql = "SELECT " +
				"id, " +
				"projectId, " +
				"pattern " +
			"FROM " +
				"resources " +
			"ORDER BY " +
				"id ASC";

			SQLiteStatement st = db.prepare(sql);

			String formatString = "%1$-4s %2$-4s %3$-32s%n";

			console.printf(formatString, "Id", "Project Id", "Pattern");

			while(st.step())
			{
				console.printf(formatString, 
					st.columnString(0), 
					st.columnString(1), 
					st.columnString(2));
			}
		}
		catch(Exception e)
		{
			console.printf(e.getMessage() + "%n");

			logger.warning(e.getMessage());
		}
	}

	public void addProject(String name, String leftPath, int leftResourceId, String rightPath, int rightResourceId)
	{
		try
		{
			leftPath = normalizePath(leftPath);
			rightPath = normalizePath(rightPath);

			Resource leftResource = new Resource(db, leftResourceId);
			Resource rightResource = new Resource(db, rightResourceId);

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
			st.bind(3, leftResource.getId());
			st.bind(4, rightPath);
			st.bind(5, rightResource.getId());

			st.step();

			console.printf("Add project successful%n");
		}
		catch(Exception e)
		{
			console.printf(e.getMessage() + "%n");

			logger.warning(e.getMessage());
		}
	}

	public void addResource(String type, String name, HashMap<String, String> config)
	{
		try
		{
			// serialize object
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			ObjectOutputStream oos = new ObjectOutputStream(baos);

			oos.writeObject(config);

			oos.close();


			// insert record
			String sql = "INSERT INTO resources (" +
				"type, " +
				"name, " +
				"config " +
			") VALUES (" +
				"?, " +
				"?, " +
				"?" +
			")";

			SQLiteStatement st = db.prepare(sql);

			st.bind(1, type);
			st.bind(2, name);
			st.bind(3, baos.toByteArray());

			st.step();

			console.printf("Add resource successful%n");
		}
		catch(Exception e)
		{
			console.printf(e.getMessage() + "%n");

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
			console.printf(e.getMessage() + "%n");

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
			console.printf(e.getMessage() + "%n");

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
			console.printf(e.getMessage() + "%n");

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
			console.printf(e.getMessage() + "%n");

			logger.warning(e.getMessage());
		}
	}

	public void build()
	{
		try
		{
			String sql;


			// remove all tables
			sql = "DROP TABLE IF EXISTS projects";

			db.exec(sql);

			sql = "DROP TABLE IF EXISTS resources";

			db.exec(sql);

			sql = "DROP TABLE IF EXISTS releases";

			db.exec(sql);

			sql = "DROP TABLE IF EXISTS exclude";

			db.exec(sql);


			// projects
			sql = "CREATE TABLE IF NOT EXISTS projects (" +
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
				"type VARCHAR(16)," +
				"name VARCHAR(128)," +
				"config BLOB" +
			")";

			db.exec(sql);

			sql = "INSERT INTO resources (" +
				"type, " +
				"name, " +
				"config " +
			") VALUES (" +
				"'SYSTEM', " +
				"'Local', " +
				"''" +
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
				"pattern VARCHAR" +
			")";

			db.exec(sql);

			console.printf("Building tables successful%n");
		}
		catch(Exception e)
		{
			console.printf(e.getMessage() + "%n");

			logger.warning(e.getMessage());
		}
	}

	public void info(int projectId)
	{
		try
		{
			Project project = new Project(db, projectId);

			String sql = "SELECT " +
				"id, " +
				"pattern " +
			"FROM " +
				"exclude " +
			"WHERE " +
				"projectId = " + project.getId();

			SQLiteStatement st = db.prepare(sql);

			String formatString = "%1$-12s %2$-32s%n";

			console.printf("Id: " + project.getId() + "%n");
			console.printf("Name: " + project.getName() + "%n");
			console.printf("Date: " + project.getDate() + "%n");
			console.printf("Left path: " + project.getLeftPath() + "%n");
			console.printf("Right path: " + project.getRightPath() + "%n");

			console.printf("%n-- Exclude rules:%n");

			formatString = "%1$-4s %2$-16s%n";

			console.printf(formatString, "Id", "Pattern");

			while(st.step())
			{
				console.printf(formatString, st.columnString(0), st.columnString(1));
			}
		}
		catch(Exception e)
		{
			console.printf(e.getMessage() + "%n");

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
		console.printf("Kadabra is an application to mirror a resource to another resource.%n");
		console.printf("A resource can be a local folder or an remote FTP folder.%n");
		console.printf("You can create multiple projects wich are stored in an SQLite database.%n");
		console.printf("With the option --status [id] you can see wich changes are made and%n");
		console.printf("with --release [id] you can mirror the project.%n");
	}

	public void help()
	{
		String formatString = "%1$-32s %2$-64s%n";

		console.printf(formatString, "Argument", "Description");
		console.printf(formatString, "--status [projectId]", "Shows wich files will be updated but without making any actions");
		console.printf(formatString, "--release [projectId]", "Mirros the local folder to the remote folder");
		console.printf(formatString, "--list", "List all projects with their project id");
		console.printf(formatString, "--listResource", "List all resources with their resource id");
		console.printf(formatString, "--listExclude", "List all excludes with their exclude id");
		console.printf(formatString, "--add", "Add a new project to the database");
		console.printf(formatString, "--addResource", "Add a new resource wich can be used by a project");
		console.printf(formatString, "--addExclude", "Add a new exclude regexp to an project. Files or dirs wich match the pattern are excluded.");
		console.printf(formatString, "--del [projectId]", "Delete a project");
		console.printf(formatString, "--delResource [resourceId]", "Delete a project");
		console.printf(formatString, "--delExclude [excludeId]", "Delete a project");
		console.printf(formatString, "--info [projectId]", "Get informations about a project");
		console.printf(formatString, "--build", "Build the tables in the database if it not exists");
		console.printf(formatString, "--about", "Shows informations about the application");
	}

	private void mirrorFolder(Project project, boolean testRun, String path) throws Exception
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
	
	private String trimString(String str, int length)
	{
		if(str.length() > length)
		{
			return "..." + str.substring(str.length() - (length - 3));
		}
		else
		{
			return str;
		}
	}
	
	private String normalizePath(String path)
	{
		path = path.trim();

		if(path.charAt(path.length() - 1) == '/')
		{
			path = path.substring(0, path.length() - 1);
		}

		return path;
	}
}