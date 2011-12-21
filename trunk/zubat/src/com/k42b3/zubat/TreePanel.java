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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.ServiceItem;

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
	private static final long serialVersionUID = 1L;

	private Zubat instance;

	private JTree tree;
	private DefaultTreeModel model;
	private JButton btnRefresh;

	private ServiceItem page;
	
	public TreePanel(Zubat instance) throws Exception
	{
		this.instance = instance;

		this.setLayout(new BorderLayout());

		model = new DefaultTreeModel(this.loadTree());

		tree = new JTree(model);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setDragEnabled(true);
		tree.setTransferHandler(new TreeTransferHandler());
		//tree.addTreeSelectionListener(new TreeListener());

		/*
		ImageIcon pageIcon = new ImageIcon(this.getClass().getResource("/page/normal.png"), "Page");

		if(pageIcon != null) 
		{
		    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		    renderer.setLeafIcon(pageIcon);
		    renderer.setOpenIcon(pageIcon);
		    renderer.setClosedIcon(pageIcon);

		    tree.setCellRenderer(renderer);
		}
		*/

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
		page = instance.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/content/page");

		if(page == null)
		{
			throw new Exception("Could not find page service");
		}

		String url = page.getUri();

		if(url != null)
		{
			Document doc = instance.getHttp().requestXml(Http.GET, url + "/buildTree");

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

			if(childs.item(i).getNodeName().equals("id"))
			{
				id = Integer.parseInt(childs.item(i).getTextContent());
			}

			if(childs.item(i).getNodeName().equals("text"))
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

				if(childs.item(i).getNodeName().equals("children"))
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

		public boolean equals(Object obj)
		{
			if(obj instanceof PageItem)
			{
				return ((PageItem) obj).getId() == this.getId();
			}

			return false;
		}
	}

	class TreeListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent e) 
		{
			if(e.getNewLeadSelectionPath() != null)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();

				if(node != null)
				{
					PageItem item = (PageItem) node.getUserObject();
					Component com = instance.loadContainer(page);

					if(com instanceof com.k42b3.zubat.basic.Container)
					{
						com.k42b3.zubat.basic.Container con = (com.k42b3.zubat.basic.Container) com;

						con.setSelectedId(item.getId());
						con.setSelectedIndex(2); // update
					}
				}
			}
			else
			{
				instance.loadContainer(page);
			}
		}
	}

	class TreeTransferHandler extends TransferHandler
	{
		private DataFlavor pageFlavor = new DataFlavor(PageTransferable.class, null);

		public boolean importData(TransferSupport support)
		{
			Component source = support.getComponent();
			Transferable data = support.getTransferable();

			try
			{
				JTree tree = (JTree) source;
				TreePath path = tree.getSelectionPath();
				Object srcData = data.getTransferData(pageFlavor);

				if(path != null && srcData instanceof DefaultMutableTreeNode)
				{
					DefaultMutableTreeNode src = (DefaultMutableTreeNode) srcData;
					DefaultMutableTreeNode dest = (DefaultMutableTreeNode) path.getLastPathComponent();

					// remove src node
					//DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();
					//tm.removeNodeFromParent(src);

					// add dest node
					if(dest.isLeaf())
					{
						DefaultMutableTreeNode parent = (DefaultMutableTreeNode) dest.getParent();

						if(parent != null)
						{
							System.out.println(parent + " parent insert " + src + " at pos " + parent.getIndex(dest));
							parent.insert(src, parent.getIndex(dest));
						}
						else
						{
							System.out.println(dest + " has no parent");
						}
					}
					else
					{
						System.out.println("insert");
						dest.add(src);
					}
				}

				return true;
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
				
				return false;
			}
		}

		public boolean canImport(TransferSupport support)
		{
			if(!support.isDrop() || !support.isDataFlavorSupported(pageFlavor))
			{
				return false;
			}

			if(support.getDropAction() == MOVE)
			{
				support.setShowDropLocation(true);

				return true;
			}

			return false;
		}

		public int getSourceActions(JComponent c)
		{
			return MOVE;
		}

		protected Transferable createTransferable(JComponent c)
		{
			JTree tree = (JTree) c;
			TreePath path = tree.getSelectionPath();

			if(path != null)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

				// we can only move under the root
				if(((PageItem) node.getUserObject()).getId() > 1)
				{
					return new PageTransferable(node);
				}
			}

			return null;
		}

		protected void exportDone(JComponent source, Transferable data, int action)
		{
			// export done
		}

		private class PageTransferable implements Transferable
		{
			private DefaultMutableTreeNode page;

			public PageTransferable(DefaultMutableTreeNode page)
			{
				this.page = page;
			}

			public DataFlavor[] getTransferDataFlavors() 
			{
				DataFlavor[] flavors = {pageFlavor};

				return flavors;
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) 
			{
				return flavor.equals(pageFlavor);
			}

			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException 
			{
				return page;
			}
		}
	}
}
