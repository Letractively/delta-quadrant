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

import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import com.k42b3.kadabra.record.Exclude;
import com.k42b3.kadabra.record.Project;
import com.k42b3.kadabra.record.Release;
import com.k42b3.kadabra.record.Resource;

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
	public static String version = "0.0.4 beta";

	private Console console;
	private Logger logger;

	private ArrayList<String> log;
	private SimpleDateFormat logDateFormat;

	public Kadabra()
	{
		this.console = System.console();
		this.logger = Logger.getLogger("com.k42b3.kadabra");

		this.logDateFormat = new SimpleDateFormat("HH:mm:ss");
	}

	public void status(int projectId)
	{
		try
		{
			this.log = new ArrayList<String>();

			Project project = Project.getProjectById(projectId);

			project.getRightHandler().loadMap();

			this.mirrorFolder(project, true, "");

			console.printf("Found " + this.log.size() + " changes%n");

			project.close();
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}

	public void release(int projectId)
	{
		try
		{
			this.log = new ArrayList<String>();

			Project project = Project.getProjectById(projectId);

			project.getRightHandler().loadMap();

			this.mirrorFolder(project, false, "");

			console.printf("Found " + this.log.size() + " changes%n");

			// rebuild map
			FileMap.generate(project.getRightHandler());

			// write log
			this.writeLog(project);

			// add release
			project.addRelease();

			project.close();

			console.printf("Project release successful%n");
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}

	public void listProject()
	{
		try
		{
			ArrayList<Project> projects = Project.getProjects();

			String formatString = "%1$-4s %2$-16s %3$-32s %4$-32s%n";

			console.printf(formatString, "Id", "Name", "leftPath", "rightPath");

			for(int i = 0; i < projects.size(); i++)
			{
				console.printf(formatString, 
						projects.get(i).getId(),
						projects.get(i).getName(),
						trimString(projects.get(i).getLeftPath(), 32),
						trimString(projects.get(i).getRightPath(), 32));
			}
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}

	public void listResource()
	{
		try
		{
			ArrayList<Resource> resources = Resource.getResources();

			String formatString = "%1$-4s %2$-8s %3$-32s%n";

			console.printf(formatString, "Id", "Type", "Name");

			for(int i = 0; i < resources.size(); i++)
			{
				console.printf(formatString, 
						resources.get(i).getId(),
						resources.get(i).getType(),
						resources.get(i).getName());
			}
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}

	public void addProject(String name, String leftPath, int leftResourceId, String rightPath, int rightResourceId)
	{
		try
		{
			Project project = new Project();

			project.setName(name);
			project.setLeftPath(leftPath);
			project.setLeftResourceId(leftResourceId);
			project.setRightPath(rightPath);
			project.setRightResourceId(rightResourceId);

			project.insert();

			console.printf("Add project successful%n");
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}

	public void addResource(String type, String name, HashMap<String, String> config)
	{
		try
		{
			Resource resource = new Resource();

			resource.setType(type);
			resource.setName(name);
			resource.setConfig(config);

			resource.insert();

			console.printf("Add resource successful%n");
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}

	public void addExclude(int projectId, String pattern)
	{
		try
		{
			Exclude exclude = new Exclude();

			exclude.setProjectId(projectId);
			exclude.setPattern(pattern);

			exclude.insert();

			console.printf("Add exclude successful%n");
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}

	public void deleteProject(int projectId)
	{
		try
		{
			Project project = Project.getProjectById(projectId);

			project.delete();

			console.printf("Delete project " + projectId + " successful%n");
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}

	public void deleteResource(int resourceId)
	{
		try
		{
			Resource resource = Resource.getResourceById(resourceId);

			resource.delete();

			console.printf("Delete resource " + resourceId + " successful%n");
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}

	public void deleteExclude(int excludeId)
	{
		try
		{
			Exclude exclude = Exclude.getExcludeById(excludeId);

			exclude.delete();

			console.printf("Delete exclude " + excludeId + " successful%n");
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}

	public void build()
	{
		try
		{
			Db.getInstance().exec("DROP TABLE IF EXISTS projects");
			Project.setupTable();

			Db.getInstance().exec("DROP TABLE IF EXISTS resources");
			Resource.setupTable();

			Db.getInstance().exec("DROP TABLE IF EXISTS releases");
			Release.setupTable();

			Db.getInstance().exec("DROP TABLE IF EXISTS exclude");
			Exclude.setupTable();

			console.printf("Building tables successful%n");
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}

	public void info(int projectId)
	{
		try
		{
			Project project = Project.getProjectById(projectId);

			String formatString = "%1$-12s %2$-32s%n";

			console.printf("Id: " + project.getId() + "%n");
			console.printf("Name: " + project.getName() + "%n");
			console.printf("Date: " + project.getDate() + "%n");
			console.printf("Left path: " + project.getLeftPath() + "%n");
			console.printf("Right path: " + project.getRightPath() + "%n");


			formatString = "%1$-4s %2$-16s%n";

			// excludes
			console.printf("%n-- Exclude rules:%n");
			console.printf(formatString, "Id", "Pattern");

			ArrayList<Exclude> excludes = project.getExcludes();

			for(int i = 0; i < excludes.size(); i++)
			{
				console.printf(formatString, excludes.get(i).getId(), excludes.get(i).getPattern());
			}

			// releases
			console.printf("%n-- Latest releases:%n");
			console.printf(formatString, "Id", "Date");

			ArrayList<Release> releases = project.getReleases(8);

			for(int i = 0; i < releases.size(); i++)
			{
				console.printf(formatString, releases.get(i).getId(), releases.get(i).getDate());
			}
		}
		catch(Exception e)
		{
			handleException(e);
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

		console.printf("Kadabra, Version: " + Kadabra.version + "%n");
		console.printf("Usage:%n");
		console.printf("    java -jar kadabra.jar [command]%n");
		console.printf("%n");
		console.printf("Available commands:%n");
		console.printf(formatString, "status [projectId]", "Shows wich files will be updated but without making any actions");
		console.printf(formatString, "release [projectId]", "Mirros the local folder to the remote folder");
		console.printf(formatString, "list", "List all projects with their project id");
		console.printf(formatString, "list resource", "List all resources with their resource id");
		console.printf(formatString, "add", "Add a new project to the database");
		console.printf(formatString, "add resource", "Add a new resource wich can be used by a project");
		console.printf(formatString, "add exclude", "Add a new exclude regexp to an project. Files or dirs wich match the pattern are excluded.");
		console.printf(formatString, "del", "Delete a project");
		console.printf(formatString, "del resource", "Delete a project");
		console.printf(formatString, "del exclude", "Delete a project");
		console.printf(formatString, "info [projectId]", "Get informations about a project");
		console.printf(formatString, "build", "Build the tables in the database if it not exists");
		console.printf(formatString, "about", "Shows informations about the application");
	}

	private void mirrorFolder(Project project, boolean testRun, String path) throws Exception
	{
		// get files
		Item[] leftFiles = project.getLeftHandler().getFiles(path);
		Item[] rightFiles = project.getRightHandler().getMap().getFiles(path);

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
				Item rightItem = this.getFolder(leftItem, rightFiles);

				if(rightItem != null)
				{
					this.mirrorFolder(project, testRun, path + "/" + leftItem.getName());
				}
				else
				{
					this.addLog("A " + path + "/" + leftItem.getName());

					if(!testRun)
					{
						project.getRightHandler().makeDirectory(path + "/" + leftItem.getName());
					}

					this.uploadDir(project, testRun, path + "/" + leftItem.getName());
				}
			}

			if(leftItem.isFile())
			{
				Item rightItem = this.getFile(leftItem, rightFiles);

				if(rightItem != null)
				{
					// compare content
					if(!leftItem.getMd5().equals(rightItem.getMd5()))
					{
						this.addLog("U " + path + "/" + leftItem.getName());

						if(!testRun)
						{
							byte[] leftContent = project.getLeftHandler().getContent(path + "/" + leftItem.getName());

							project.getRightHandler().uploadFile(path + "/" + leftItem.getName(), leftContent);
						}
					}
				}
				else
				{
					this.addLog("A " + path + "/" + leftItem.getName());

					if(!testRun)
					{
						byte[] leftContent = project.getLeftHandler().getContent(path + "/" + leftItem.getName());

						project.getRightHandler().uploadFile(path + "/" + leftItem.getName(), leftContent);
					}
				}
			}
		}
	}

	private Item getFolder(Item file, Item[] files)
	{
		if(files != null)
		{
			for(int i = 0; i < files.length; i++)
			{
				if(files[i].isDirectory() && files[i].getName().equals(file.getName()))
				{
					return files[i];
				}
			}
		}

		return null;
	}

	private Item getFile(Item file, Item[] files)
	{
		if(files != null)
		{
			for(int i = 0; i < files.length; i++)
			{
				if(files[i].isFile() && files[i].getName().equals(file.getName()))
				{
					return files[i];
				}
			}
		}

		return null;
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
				this.addLog("A " + path + "/" + leftItem.getName());

				if(!testRun)
				{
					project.getRightHandler().makeDirectory(path + "/" + leftItem.getName());
				}

				this.uploadDir(project, testRun, path + "/" + leftItem.getName());
			}

			if(leftItem.isFile())
			{
				this.addLog("A " + path + "/" + leftItem.getName());

				if(!testRun)
				{
					byte[] content = project.getLeftHandler().getContent(path + "/" + leftItem.getName());

					project.getRightHandler().uploadFile(path + "/" + leftItem.getName(), content);
				}
			}
		}
	}

	private void writeLog(Project project) throws Exception
	{
		String file = "release." + project.getId() + ".log";
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

		pw.println("Release " + project.getName() + " on " + sdf.format(new Date()));
		pw.println("Source: " + project.getLeftPath());
		pw.println("Destination: " + project.getRightPath());
		pw.println("");

		for(int i = 0; i < log.size(); i++)
		{
			pw.println(log.get(i));
		}

		pw.close();
	}

	private void addLog(String msg)
	{
		this.log.add("[" + logDateFormat.format(new Date()) + "]: " + msg);

		console.printf(msg + "%n");
	}

	/**
	 * Converts content to an UTF-8 string and removes all whispace for correct 
	 * comparsion of files
	 * 
	 * @param byte[] content
	 * @return String
	 */
	public static String normalizeContent(byte[] content)
	{
		String str = new String(content, Charset.forName("UTF-8"));
		StringBuilder result = new StringBuilder();

		for(int i = 0; i < str.length(); i++)
		{
			if(!Character.isWhitespace(str.charAt(i)))
			{
				result.append(str.charAt(i));
			}
		}

		return result.toString();
	}

	public static String trimString(String str, int length)
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

	public static void handleException(Exception e)
	{
		System.console().printf(e.getMessage() + "%n");

		e.printStackTrace();

		//Logger.getLogger("com.k42b3.kadabra").warning(e.getMessage());
	}
}
