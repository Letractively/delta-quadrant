/**
 * Kadabra
 * 
 * Kadabra is an application to mirror a left folder to a right folder.
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

/**
 * Item
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 67 $
 */
public class Item 
{
	public static int FILE = 0x1;
	public static int DIRECTORY = 0x2;

	private String name;
	private String path;
	private String absolutePath;
	private int type;

	public Item(String name, String path, String absolutePath, int type)
	{
		this.setName(name);
		this.setPath(path);
		this.setAbsolutePath(absolutePath);
		this.setType(type);
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getPath() 
	{
		return path;
	}

	public void setPath(String path) 
	{
		this.path = path;
	}

	public String getAbsolutePath() 
	{
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) 
	{
		this.absolutePath = absolutePath;
	}

	public int getType() 
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public boolean isFile()
	{
		return type == Item.FILE;
	}
	
	public boolean isDirectory()
	{
		return type == Item.DIRECTORY;
	}
}
