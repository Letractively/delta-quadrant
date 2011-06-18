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

import java.util.ArrayList;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteStatement;

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
	private SQLiteConnection db;
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

	public Project(SQLiteConnection db, int id) throws Exception
	{
		this.db = db;

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

		SQLiteStatement st = db.prepare(sql);

		st.step();

		if(st.hasRow())
		{
			this.id = st.columnInt(0);
			this.name = st.columnString(1);
			this.date = st.columnString(2);

			this.leftPath = st.columnString(3);
			this.leftResourceId = st.columnInt(4);
			this.leftHandler = HandlerFactory.factory(new Resource(db, this.leftResourceId), this.leftPath);

			this.rightPath = st.columnString(5);
			this.rightResourceId = st.columnInt(6);
			this.rightHandler = HandlerFactory.factory(new Resource(db, this.rightResourceId), this.rightPath);

			this.exclude = this.buildExclude();
		}
		else
		{
			throw new Exception("Invalid project id");
		}
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getDate()
	{
		return date;
	}

	public String getLeftPath() 
	{
		return leftPath;
	}

	public String getRightPath() 
	{
		return rightPath;
	}

	public int getLeftResourceId()
	{
		return leftResourceId;
	}

	public int getRightResourceId()
	{
		return rightResourceId;
	}

	public HandlerAbstract getLeftHandler() throws Exception
	{
		return leftHandler;
	}

	public HandlerAbstract getRightHandler() throws Exception
	{
		return rightHandler;
	}

	public ArrayList<String> getExclude() throws Exception
	{
		return exclude;
	}

	public void close() throws Exception
	{
		leftHandler.close();
		rightHandler.close();
	}

	public void addRelease() throws Exception
	{
		String sql = "INSERT INTO releases (" +
			"projectId, " +
			"date" +
		") VALUES (" +
			"?, " +
			"datetime()" +
		")";

		SQLiteStatement st = db.prepare(sql);

		st.bind(1, this.getId());

		st.step();
	}
	
	private ArrayList<String> buildExclude() throws Exception
	{
		ArrayList<String> exclude = new ArrayList<String>();

		String sql = "SELECT " +
			"pattern " +
		"FROM " +
			"exclude " +
		"WHERE " +
			"projectId = " + this.getId();

		SQLiteStatement st = db.prepare(sql);

		while(st.step())
		{
			exclude.add(st.columnString(0));
		}

		return exclude;
	}
}
