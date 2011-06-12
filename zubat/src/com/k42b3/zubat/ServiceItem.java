/**
 * zubat
 * 
 * An java application to access the API of amun. It is used to debug and
 * control a website based on amun. This is the reference implementation 
 * howto access the api. So feel free to hack and extend.
 * 
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
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

package com.k42b3.zubat;

import java.util.ArrayList;

/**
 * ServiceItem
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class ServiceItem 
{
	private String uri;
	private ArrayList<String> types;

	public ServiceItem(String uri, ArrayList<String> types)
	{
		this.setUri(uri);
		this.setTypes(types);
	}

	public String getUri() 
	{
		return uri;
	}

	public void setUri(String uri) 
	{
		this.uri = uri;
	}
	
	public ArrayList<String> getTypes() 
	{
		return types;
	}

	public void setTypes(ArrayList<String> types) 
	{
		this.types = types;
	}

	public String getTypeStartsWith(String prefix)
	{
		for(int i = 0; i < this.types.size(); i++)
		{
			if(this.types.get(i).startsWith(prefix))
			{
				return this.types.get(i);
			}
		}

		return null;
	}

	public boolean hasType(String type)
	{
		return this.types.contains(type);
	}

	public boolean hasTypeStartsWith(String prefix)
	{
		return this.getTypeStartsWith(prefix) != null;
	}

	public String toString()
	{
		return uri;
	}
}
