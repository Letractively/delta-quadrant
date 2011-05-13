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

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
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
	private String leftPath;
	private int leftResourceId;
	private String rightPath;
	private int rightResourceId;

	public Project(SQLiteConnection db, int id) throws Exception
	{
		this.db = db;

		String sql = "SELECT " +
			"id, " +
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
			this.leftPath = st.columnString(1);
			this.leftResourceId = st.columnInt(2);
			this.rightPath = st.columnString(3);
			this.rightResourceId = st.columnInt(4);
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
		String sql = "SELECT " +
			"type, " +
			"config " +
		"FROM " +
			"resources " +
		"WHERE " +
			"id = " + this.getLeftResourceId();

		SQLiteStatement st = db.prepare(sql);

		st.step();

		String type = st.columnString(0);
		ByteArrayInputStream bais = new ByteArrayInputStream(st.columnString(1).getBytes());

		ObjectInputStream ois = new ObjectInputStream(bais);

		Resource resource = (Resource) ois.readObject();

		return this.getHandler(resource, type);
	}

	public HandlerAbstract getRightHandler() throws Exception
	{
		String sql = "SELECT " +
			"type, " +
			"config " +
		"FROM " +
			"resources " +
		"WHERE " +
			"id = " + this.getRightResourceId();

		SQLiteStatement st = db.prepare(sql);

		st.step();

		String type = st.columnString(0);
		ByteArrayInputStream bais = new ByteArrayInputStream(st.columnString(1).getBytes());

		ObjectInputStream ois = new ObjectInputStream(bais);

		Resource resource = (Resource) ois.readObject();

		return this.getHandler(resource, type);
	}

	public ArrayList<String> getExclude() throws Exception
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

	public void close() throws Exception
	{
		this.getLeftHandler().close();
		this.getRightHandler().close();
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

	private HandlerAbstract getHandler(Resource resource, String type) throws Exception
	{
		if(type.equals("SYSTEM"))
		{
			return new com.k42b3.kadabra.handler.System(resource, this.getLeftPath());
		}
		else if(type.equals("FTP"))
		{
			return new com.k42b3.kadabra.handler.Ftp(resource, this.getLeftPath());
		}
		else if(type.equals("SSH"))
		{
			return new com.k42b3.kadabra.handler.Ssh(resource, this.getLeftPath());
		}
		else
		{
			throw new Exception("Invalid resource type");
		}
	}
}
