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

package com.k42b3.kadabra.record;

import java.util.ArrayList;

import com.almworks.sqlite4java.SQLiteStatement;
import com.k42b3.kadabra.Db;
import com.k42b3.kadabra.FileMap;
import com.k42b3.kadabra.HandlerAbstract;
import com.k42b3.kadabra.HandlerFactory;

/**
 * Project
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 197 $
 */
public class Project extends Record
{
	private int id;
	private String name;
	private String date;
	
	private String leftPath;
	private int leftResourceId;
	private HandlerAbstract leftHandler;
	
	private String rightPath;
	private int rightResourceId;
	private HandlerAbstract rightHandler;
	
	private ArrayList<String> exclude;

	public Project() throws Exception
	{
		super();
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getLeftPath()
	{
		return leftPath;
	}

	public void setLeftPath(String leftPath)
	{
		this.leftPath = normalizePath(leftPath);
	}

	public int getLeftResourceId()
	{
		return leftResourceId;
	}

	public void setLeftResourceId(int leftResourceId)
	{
		this.leftResourceId = leftResourceId;
	}

	public HandlerAbstract getLeftHandler()
	{
		return leftHandler;
	}

	public void setLeftHandler(HandlerAbstract leftHandler)
	{
		this.leftHandler = leftHandler;
	}

	public String getRightPath()
	{
		return rightPath;
	}

	public void setRightPath(String rightPath)
	{
		this.rightPath = normalizePath(rightPath);
	}

	public int getRightResourceId()
	{
		return rightResourceId;
	}

	public void setRightResourceId(int rightResourceId)
	{
		this.rightResourceId = rightResourceId;
	}

	public HandlerAbstract getRightHandler()
	{
		return rightHandler;
	}

	public void setRightHandler(HandlerAbstract rightHandler)
	{
		this.rightHandler = rightHandler;
	}

	public ArrayList<String> getExclude()
	{
		return exclude;
	}

	public void setExclude(ArrayList<String> exclude)
	{
		this.exclude = exclude;
	}

	public void addRelease() throws Exception
	{
		Release release = new Release();

		release.setProjectId(this.getId());

		release.insert();
	}

	public ArrayList<Release> getReleases(int limit) throws Exception
	{
		ArrayList<Release> releases = new ArrayList<Release>();

		String sql = "SELECT " +
			"id " +
		"FROM " +
			"releases " +
		"ORDER BY " +
			"date DESC";

		if(limit > 0)
		{
			sql+= " LIMIT " + limit;
		}

		SQLiteStatement st = Db.getInstance().query(sql);

		while(st.step())
		{
			releases.add(Release.getReleaseById(st.columnInt(0)));
		}

		return releases;
	}

	public ArrayList<Release> getReleases() throws Exception
	{
		return this.getReleases(-1);
	}

	public ArrayList<Exclude> getExcludes() throws Exception
	{
		ArrayList<Exclude> excludes = new ArrayList<Exclude>();

		String sql = "SELECT " +
			"id " +
		"FROM " +
			"exclude " +
		"ORDER BY " +
			"id DESC";

		SQLiteStatement st = Db.getInstance().query(sql);

		while(st.step())
		{
			excludes.add(Exclude.getExcludeById(st.columnInt(0)));
		}

		return excludes;
	}

	public Resource getLeftResource() throws Exception
	{
		return Resource.getResourceById(this.getLeftResourceId());
	}

	public Resource getRightResource() throws Exception
	{
		return Resource.getResourceById(this.getRightResourceId());		
	}

	public void close() throws Exception
	{
		if(this.leftHandler != null)
		{
			this.leftHandler.close();
		}

		if(this.rightHandler != null)
		{
			this.rightHandler.close();
		}
	}

	public void insert() throws Exception
	{
		Resource leftResource = Resource.getResourceById(this.getLeftResourceId());
		Resource rightResource = Resource.getResourceById(this.getRightResourceId());

		HandlerAbstract handlerLeft = HandlerFactory.factory(leftResource, this.getLeftPath());
		HandlerAbstract handlerRight = HandlerFactory.factory(rightResource, this.getRightPath());

		// build map
		FileMap.generate(handlerRight);


		String sql = "INSERT INTO " +
			"projects " +
		"SET " +
			"name = ?, " +
			"date = NOW(), " + 
			"leftPath = ?, " + 
			"leftResourceId = ?, " + 
			"rightPath = ?, " + 
			"rightResourceId = ?";
		
		SQLiteStatement st = Db.getInstance().query(sql);

		st.bind(1, this.getName());
		st.bind(2, this.getDate());
		st.bind(3, this.getLeftPath());
		st.bind(4, this.getLeftResourceId());
		st.bind(5, this.getRightPath());
		st.bind(6, this.getRightResourceId());

		st.step();
	}

	public void update() throws Exception
	{
		String sql = "UPDATE " +
			"projects " +
		"SET " +
			"name = ?, " +
			"date = ?, " +
			"leftPath = ?, " +
			"leftResourceId = ?, " +
			"rightPath = ?, " +
			"rightResourceId = ? " +
		"WHERE " +
			"id = " + this.getId();

		SQLiteStatement st = Db.getInstance().query(sql);

		st.bind(1, this.getName());
		st.bind(2, this.getDate());
		st.bind(3, this.getLeftPath());
		st.bind(4, this.getLeftResourceId());
		st.bind(5, this.getRightPath());
		st.bind(6, this.getRightResourceId());

		st.step();
	}

	public void delete() throws Exception
	{
		String sql = "DELETE FROM " +
			"projects " +
		"WHERE " +
			"id = " + this.getId();

		// delete releases
		ArrayList<Release> releases = this.getReleases();
		
		for(int i = 0; i < releases.size(); i++)
		{
			releases.get(i).delete();
		}

		// delete exclude
		ArrayList<Exclude> excludes = this.getExcludes();

		for(int i = 0; i < excludes.size(); i++)
		{
			excludes.get(i).delete();
		}
	}

	public static Project getProjectById(int id) throws Exception
	{
		String sql = "SELECT " +
			"id, " +
			"name, " +
			"date, " +
			"leftPath, " +
			"leftResourceId, " +
			"rightPath, " +
			"rightResourceId " +
		"FROM " +
			"projects " +
		"WHERE " +
			"id = " + id;

		SQLiteStatement st = Db.getInstance().query(sql);

		st.step();

		if(st.hasRow())
		{
			Project project = new Project();
			
			project.id = st.columnInt(0);
			project.name = st.columnString(1);
			project.date = st.columnString(2);

			project.leftPath = st.columnString(3);
			project.leftResourceId = st.columnInt(4);
			project.leftHandler = HandlerFactory.factory(Resource.getResourceById(project.getLeftResourceId()), project.getLeftPath());

			project.rightPath = st.columnString(3);
			project.rightResourceId = st.columnInt(4);
			project.rightHandler = HandlerFactory.factory(Resource.getResourceById(project.getRightResourceId()), project.getRightPath());

			project.exclude = Project.getExclude(project.getId());

			return project;
		}
		else
		{
			throw new Exception("Invalid project id");
		}
	}

	public static ArrayList<Project> getProjects() throws Exception
	{
		ArrayList<Project> projects = new ArrayList<Project>();

		String sql = "SELECT " +
			"id " +
		"FROM " +
			"projects";

		SQLiteStatement st = Db.getInstance().query(sql);

		while(st.step())
		{
			projects.add(Project.getProjectById(st.columnInt(0)));
		}

		return projects;
	}

	public static ArrayList<String> getExclude(int projectId) throws Exception
	{
		ArrayList<String> exclude = new ArrayList<String>();

		String sql = "SELECT " +
			"pattern " +
		"FROM " +
			"exclude " +
		"WHERE " +
			"projectId = " + projectId;

		SQLiteStatement st = Db.getInstance().query(sql);

		while(st.step())
		{
			exclude.add(st.columnString(0));
		}

		return exclude;
	}
	
	public static void setupTable() throws Exception
	{
		String sql = "CREATE TABLE IF NOT EXISTS projects (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"name VARCHAR(128), " +
			"leftPath VARCHAR(512)," +
			"leftResourceId INTEGER," +
			"rightPath VARCHAR(512)," +
			"rightResourceId INTEGER," +
			"date DATETIME" +
		")";

		Db.getInstance().exec(sql);
	}
	
	
	public static String normalizePath(String path)
	{
		path = path.trim();

		if(path.charAt(path.length() - 1) == '/')
		{
			path = path.substring(0, path.length() - 1);
		}

		return path;
	}
}
