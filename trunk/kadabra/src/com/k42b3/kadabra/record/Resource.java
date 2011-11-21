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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.almworks.sqlite4java.SQLiteStatement;
import com.k42b3.kadabra.Db;

/**
 * Resource
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Resource extends Record
{
	private int id;
	private String type;
	private String name;
	private HashMap<String, String> config;

	public Resource() throws Exception
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

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public HashMap<String, String> getConfig()
	{
		return config;
	}

	public void setConfig(HashMap<String, String> config)
	{
		this.config = config;
	}

	public String getString(Object key)
	{
		return config.get(key).toString();
	}

	public void insert() throws Exception
	{
		String sql = "INSERT INTO resources (" +
			"type, " +
			"name, " +
			"config " +
		") VALUES (" +
			"?, " +
			"?, " +
			"?" +
		")";

		SQLiteStatement st = Db.getInstance().query(sql);

		st.bind(1, this.getType());
		st.bind(2, this.getName());
		st.bind(3, serialize(this.getConfig()));

		st.step();
	}

	public void update() throws Exception
	{
		String sql = "UPDATE " +
			"resources " +
		"SET " +
			"type = ?, " +
			"name = ?, " +
			"config = ? " +
		"WHERE " +
			"id = " + this.getId();

		SQLiteStatement st = Db.getInstance().query(sql);

		st.bind(1, this.getType());
		st.bind(2, this.getName());
		st.bind(3, serialize(this.getConfig()));

		st.step();
	}

	public void delete() throws Exception
	{
		String sql = "DELETE FROM " +
			"resources " +
		"WHERE " +
			"id = " + this.getId();

		Db.getInstance().exec(sql);
	}

	public static Resource getResourceById(int id) throws Exception
	{
		String sql = "SELECT " +
			"id, " +
			"type, " +
			"name, " +
			"config " +
		"FROM " +
			"resources " +
		"WHERE " +
			"id = " + id;

		SQLiteStatement st = Db.getInstance().query(sql);

		st.step();

		if(st.hasRow())
		{
			Resource resource = new Resource();

			resource.id = st.columnInt(0);
			resource.type = st.columnString(1);
			resource.name = st.columnString(2);
			resource.config = Resource.unserialize(st.columnBlob(3));

			return resource;
		}
		else
		{
			throw new Exception("Invalid resource id");
		}
	}

	public static ArrayList<Resource> getResources() throws Exception
	{
		ArrayList<Resource> resources = new ArrayList<Resource>();

		String sql = "SELECT " +
			"id " +
		"FROM " +
			"resources";

		SQLiteStatement st = Db.getInstance().query(sql);

		while(st.step())
		{
			resources.add(Resource.getResourceById(st.columnInt(0)));
		}

		return resources;
	}

	public static HashMap<String, String> unserialize(byte[] data) throws Exception
	{
		HashMap<String, String> map = new HashMap<String, String>();

		if(data != null)
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(data);

			ObjectInputStream ois = new ObjectInputStream(bais);

			map = (HashMap<String, String>) ois.readObject();

			ois.close();
		}

		return map;
	}

	public static byte[] serialize(Object data) throws Exception
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ObjectOutputStream oos = new ObjectOutputStream(baos);

		oos.writeObject(data);

		oos.close();

		return baos.toByteArray();
	}
	
	public static void setupTable() throws Exception
	{
		String sql = "CREATE TABLE IF NOT EXISTS resources (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"type VARCHAR(16)," +
			"name VARCHAR(128)," +
			"config BLOB" +
		")";

		Db.getInstance().exec(sql);

		sql = "INSERT INTO resources (" +
			"type, " +
			"name, " +
			"config " +
		") VALUES (" +
			"'SYSTEM', " +
			"'Local', " +
			"''" +
		")";

		Db.getInstance().exec(sql);
	}
}
