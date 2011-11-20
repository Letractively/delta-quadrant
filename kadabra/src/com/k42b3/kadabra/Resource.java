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

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteStatement;

/**
 * Resource
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Resource extends HashMap
{
	private SQLiteConnection db;

	private int id;
	private String type;

	public Resource(SQLiteConnection db, int id) throws Exception
	{
		this.db = db;

		String sql = "SELECT " +
			"id, " +
			"type, " +
			"config " +
		"FROM " +
			"resources " +
		"WHERE " +
			"id = " + id;

		SQLiteStatement st = db.prepare(sql);

		st.step();

		if(st.hasRow())
		{
			this.id = st.columnInt(0);
			this.type = st.columnString(1);

			if(st.columnBlob(2) != null)
			{
				ByteArrayInputStream bais = new ByteArrayInputStream(st.columnBlob(2));

				ObjectInputStream ois = new ObjectInputStream(bais);

				this.putAll((HashMap<String, String>) ois.readObject());

				ois.close();
			}
		}
		else
		{
			throw new Exception("Invalid resource id");
		}
	}

	public int getId()
	{
		return id;
	}
	
	public String getType()
	{
		return type;
	}

	public String getString(Object key)
	{
		return this.get(key).toString();
	}
}
