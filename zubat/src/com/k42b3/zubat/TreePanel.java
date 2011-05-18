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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * TreePanel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 105 $
 */
public class TreePanel extends JPanel
{
	private Http http;
	private Services services;
	private DefaultMutableTreeNode root;
	private JTree tree;

	public TreePanel(Http http, Services services) throws Exception
	{
		this.http = http;
		this.services = services;

		this.setLayout(new BorderLayout());

		tree = new JTree(this.loadTree());

		this.add(new JScrollPane(tree));
	}

	private DefaultMutableTreeNode loadTree() throws Exception
	{
		String url = services.getItem("http://ns.amun-project.org/2011/amun/content/page").getUri();

		if(url != null)
		{
			Document doc = http.requestXml(Http.GET, url + "/buildTree");

			Node entry = doc.getElementsByTagName("entry").item(0);

			if(entry != null)
			{
				return this.parseTree(entry);
			}
			else
			{
				throw new Exception("Entry not found");
			}
		}
		else
		{
			throw new Exception("Content page service not found");
		}
	}

	private DefaultMutableTreeNode parseTree(Node node)
	{
		DefaultMutableTreeNode treeNode;
		NodeList childs = node.getChildNodes();

		// parse node
		int id = 0;
		String text = null;

		for(int i = 0; i < childs.getLength(); i++)
		{
			if(childs.item(i).getNodeType() != node.ELEMENT_NODE)
			{
				continue;
			}

			if(childs.item(i).getNodeName() == "id")
			{
				id = Integer.parseInt(childs.item(i).getTextContent());
			}

			if(childs.item(i).getNodeName() == "text")
			{
				text = childs.item(i).getTextContent();
			}
		}

		if(id != 0 && text != null)
		{
			treeNode = new DefaultMutableTreeNode(new PageItem(id, text));

			// parse children
			for(int i = 0; i < childs.getLength(); i++)
			{
				if(childs.item(i).getNodeType() != node.ELEMENT_NODE)
				{
					continue;
				}

				if(childs.item(i).getNodeName() == "children")
				{
					DefaultMutableTreeNode child = this.parseTree(childs.item(i));
					
					if(child != null)
					{
						treeNode.add(child);
					}
				}
			}

			return treeNode;
		}
		else
		{
			return null;
		}
	}
	
	class PageItem
	{
		private int id;
		private String text;

		public PageItem(int id, String text)
		{
			this.setId(id);
			this.setText(text);
		}

		public int getId() 
		{
			return id;
		}

		public void setId(int id) 
		{
			this.id = id;
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
	}
}
