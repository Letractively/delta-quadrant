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

import java.io.Console;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * HandlerAbstract
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public abstract class HandlerAbstract
{
	protected Console console;
	protected Logger logger;

	protected FileMap map;
	protected Resource resource;
	protected String basePath;

	public HandlerAbstract(Resource resource, String basePath)
	{
		this.console = System.console();
		this.logger = Logger.getLogger("com.k42b3.kadabra");

		this.resource = resource;
		this.basePath = basePath;
	}

	public boolean isFile(String path) throws Exception
	{
		logger.info(basePath + "/" + path);

		try
		{
			this.getContent(path);

			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public void loadMap() throws Exception
	{
		this.map = new FileMap(this);
	}

	public FileMap getMap() throws Exception
	{
		return map;
	}

	abstract public Item[] getFiles(String path) throws Exception;
	abstract public void makeDirectory(String path) throws Exception;
	abstract public byte[] getContent(String path) throws Exception;
	abstract public void uploadFile(String path, byte[] content) throws Exception;
	abstract public void close() throws Exception;
}
