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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.Services;

/**
 * TreePanel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class TreePanel extends JPanel
{
	private Http http;
	private Services services;
	private JTree tree;
	private DefaultTreeModel model;
	private JButton btnRefresh;

	public TreePanel(Http http, Services services) throws Exception
	{
		this.http = http;
		this.services = services;

		this.setLayout(new BorderLayout());

		model = new DefaultTreeModel(this.loadTree());
		tree = new JTree(model);

		this.add(new JScrollPane(tree), BorderLayout.CENTER);


		// buttons
		JPanel buttons = new JPanel();

		this.btnRefresh = new JButton("Refresh");

		this.btnRefresh.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				try
				{
					model.setRoot(loadTree());
				}
				catch(Exception ex)
				{
					Zubat.handleException(ex);
				}
			}

		});

		buttons.setLayout(new FlowLayout(FlowLayout.LEADING));

		buttons.add(this.btnRefresh);

		this.add(buttons, BorderLayout.SOUTH);
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
				return null;
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
			if(childs.item(i).getNodeType() != Node.ELEMENT_NODE)
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
				if(childs.item(i).getNodeType() != Node.ELEMENT_NODE)
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
