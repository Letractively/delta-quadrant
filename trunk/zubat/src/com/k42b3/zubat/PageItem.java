/**
 * $Id$
 * 
 * zubat
 * An java application to access the API of amun. It is used to debug and
 * control a website based on amun. This is the reference implementation 
 * howto access the api. So feel free to hack and extend.
 * 
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of zubat. zubat is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * zubat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with zubat. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.zubat;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * PageItem
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class PageItem implements Comparable<PageItem>
{
	private int id;
	private int status;
	private int sort;
	private String path;
	private String text;

	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	public int getStatus() 
	{
		return status;
	}

	public void setStatus(int status) 
	{
		this.status = status;
	}

	public int getSort() 
	{
		return sort;
	}

	public void setSort(int sort) 
	{
		this.sort = sort;
	}

	public String getPath() 
	{
		return path;
	}

	public void setPath(String path) 
	{
		this.path = path;
	}

	public String getText() 
	{
		return text;
	}

	public void setText(String text) 
	{
		this.text = text;
	}

	public String toString() 
	{
		return text;
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof PageItem)
		{
			return ((PageItem) obj).getId() == this.getId();
		}

		return false;
	}

	public int compareTo(PageItem o) 
	{
		PageItem item = (PageItem) o;

		if(item.getSort() > this.getSort())
		{
			return 1;
		}
		else if(item.getSort() < this.getSort())
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}

	public static PageItem parsePage(Node node)
	{
		NodeList childs = node.getChildNodes();
		PageItem item = new PageItem();

		for(int i = 0; i < childs.getLength(); i++)
		{
			if(childs.item(i).getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}

			if(childs.item(i).getNodeName().equals("id"))
			{
				item.setId(Integer.parseInt(childs.item(i).getTextContent()));
			}

			if(childs.item(i).getNodeName().equals("status"))
			{
				item.setStatus(Integer.parseInt(childs.item(i).getTextContent()));
			}

			if(childs.item(i).getNodeName().equals("sort"))
			{
				item.setSort(Integer.parseInt(childs.item(i).getTextContent()));
			}

			if(childs.item(i).getNodeName().equals("path"))
			{
				item.setPath(childs.item(i).getTextContent());
			}

			if(childs.item(i).getNodeName().equals("text"))
			{
				item.setText(childs.item(i).getTextContent());
			}
		}

		return item;
	}
}
