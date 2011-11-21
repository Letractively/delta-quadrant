/**
 * Kadabra
 * 
 * Kadabra is an application to mirror a source folder to a destination folder.
 * You can create multiple projects wich are stored in an SQLite database.
 * With the command status [id] you can see wich changes are made and
 * with release [id] you update the changes. You can use different handler
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

import com.almworks.sqlite4java.SQLiteStatement;
import com.k42b3.kadabra.Db;

/**
 * Exclude
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Exclude extends Record
{
	private int id;
	private int projectId;
	private String pattern;

	public Exclude() throws Exception
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

	public int getProjectId()
	{
		return projectId;
	}

	public void setProjectId(int projectId)
	{
		this.projectId = projectId;
	}

	public String getPattern()
	{
		return pattern;
	}

	public void setPattern(String pattern) throws Exception
	{
		// check pattern
		"foobar".matches(pattern);

		this.pattern = pattern;
	}

	public void insert() throws Exception
	{
		String sql = "INSERT INTO exclude (" +
			"projectId, " +
			"pattern " +
		") VALUES (" +
			"?, " +
			"?" +
		")";

		SQLiteStatement st = Db.getInstance().query(sql);

		st.bind(1, this.getProjectId());
		st.bind(2, this.getPattern());

		st.step();
	}

	public void update() throws Exception
	{
		String sql = "UPDATE " +
			"exclude " +
		"SET " +
			"projectId = ?, " +
			"pattern = ? " +
		"WHERE " +
			"id = " + this.getId();

		SQLiteStatement st = Db.getInstance().query(sql);

		st.bind(1, this.getProjectId());
		st.bind(2, this.getPattern());

		st.step();
	}

	public void delete() throws Exception
	{
		String sql = "DELETE FROM " +
			"exclude " +
		"WHERE " +
			"id = " + this.getId();

		Db.getInstance().exec(sql);
	}

	public static Exclude getExcludeById(int id) throws Exception
	{
		String sql = "SELECT " +
			"id, " +
			"projectId, " +
			"pattern " +
		"FROM " +
			"exclude " +
		"WHERE " +
			"id = " + id;

		SQLiteStatement st = Db.getInstance().query(sql);

		st.step();

		if(st.hasRow())
		{
			Exclude exclude = new Exclude();

			exclude.id = st.columnInt(0);
			exclude.projectId = st.columnInt(1);
			exclude.pattern = st.columnString(2);

			return exclude;
		}
		else
		{
			throw new Exception("Invalid exclude id");
		}
	}

	public static void setupTable() throws Exception
	{
		String sql = "CREATE TABLE IF NOT EXISTS exclude (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"projectId INTEGER," +
			"pattern VARCHAR" +
		")";

		Db.getInstance().exec(sql);
	}
}
