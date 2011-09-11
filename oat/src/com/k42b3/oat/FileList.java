/**
 * oat
 * 
 * An application with that you can make raw http requests to any url. You can 
 * save a request for later use. The application uses the java nio library to 
 * make non-blocking requests so the requests should work fluently.
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

package com.k42b3.oat;

import java.io.File;
import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * FileList
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class FileList implements ListModel
{
	ArrayList<String> files = new ArrayList<String>();
	ArrayList<ListDataListener> listener = new ArrayList<ListDataListener>();

	public FileList()
	{
		this.load();
	}

	public void load()
	{
		this.files.clear();

		File dir = new File("."); 

		File[] files = dir.listFiles();
		
		for(int i = 0; i < files.length; i++)
		{
			if(files[i].isFile() && files[i].getName().endsWith(".xml"))
			{
				this.files.add(files[i].getName());
			}
		}
		
		if(this.files.isEmpty())
		{
			this.files.add("No files found");
		}
		
		
		for(int j = 0; j < listener.size(); j++)
		{
			listener.get(j).contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, this.files.size() - 1));
		}
	}

	public void addListDataListener(ListDataListener l) 
	{
		this.listener.add(l);
	}

	public Object getElementAt(int index) 
	{
		return this.files.get(index);
	}

	public int getSize() 
	{
		return this.files.size();
	}

	public void removeListDataListener(ListDataListener l) 
	{
		this.listener.remove(l);
	}
}
