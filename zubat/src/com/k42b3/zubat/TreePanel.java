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
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
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
	private ServiceItem page;
	private DefaultTreeModel model;

	private JTree tree;
	private boolean isBusy = false;

	private Logger logger = Logger.getLogger("com.k42b3.zubat");

	public TreePanel(Zubat instance) throws Exception
	{
		this.instance = instance;
		this.page = instance.getAvailableServices().getItem("http://ns.amun-project.org/2011/amun/content/page");
		this.model = new DefaultTreeModel(this.loadTree());

		if(page == null)
		{
			throw new Exception("Could not find page service");
		}

		this.buildComponent();
	}

	private void buildComponent()
	{
		this.setLayout(new BorderLayout());

		this.add(this.buildTree(), BorderLayout.CENTER);
		this.add(this.buildButtons(), BorderLayout.SOUTH);
	}

	private Component buildTree()
	{
		tree = new JTree(model);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		//tree.setDragEnabled(true);
		//tree.setTransferHandler(new TreeTransferHandler());
		tree.addTreeSelectionListener(new TreeListener());

		return new JScrollPane(tree);
	}

	private Component buildButtons()
	{
		JPanel buttons = new JPanel();

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				try
				{
					reload();
				}
				catch(Exception ex)
				{
					Zubat.handleException(ex);
				}
			}

		});

		buttons.setLayout(new FlowLayout(FlowLayout.LEADING));
		buttons.add(btnRefresh);

		return buttons;
	}
	
	
	private DefaultMutableTreeNode loadTree() throws Exception
	{
		String url = page.getUri();

		if(url != null)
		{
			Document doc = instance.getHttp().requestXml(Http.GET, url + "/tree");

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
		PageItem item = PageItem.parsePage(node);

		if(item != null)
		{
			treeNode = new DefaultMutableTreeNode(item);

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

	private void reload() throws Exception
	{
		model.setRoot(loadTree());
	}

	private void moveNode(int id, int sort) throws Exception
	{
		if(!isBusy)
		{
			isBusy = true;

			// header
			HashMap<String, String> header = new HashMap<String, String>();

			header.put("Content-Type", "application/xml");
			header.put("X-HTTP-Method-Override", "PUT");

			// body
			String body = "<request><id>" + id + "</id><sort>" + sort + "</sort></request>";

			// request
			instance.getHttp().requestXml(Http.POST, page.getUri(), header, body);

			// reload
			reload();

			isBusy = false;
		}
		else
		{
			throw new Exception("Busy ...");
		}
	}

	private void reparentNode(int id, int parentId) throws Exception
	{
		if(!isBusy)
		{
			isBusy = true;

			// header
			HashMap<String, String> header = new HashMap<String, String>();

			header.put("Content-Type", "application/xml");
			header.put("X-HTTP-Method-Override", "PUT");

			// body
			String body = "<request><id>" + id + "</id><parentId>" + parentId + "</parentId></request>";

			// request
			instance.getHttp().requestXml(Http.POST, page.getUri(), header, body);

			// reload
			reload();

			isBusy = false;
		}
		else
		{
			throw new Exception("Busy ...");
		}
	}

	private class TreeListener implements TreeSelectionListener
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

	private class TreeTransferHandler extends TransferHandler
	{
		private DataFlavor pageFlavor = new DataFlavor(PageTransferable.class, null);

		public boolean importData(TransferSupport support)
		{
			Component source = support.getComponent();
			Transferable data = support.getTransferable();

			try
			{
				JTree tree = (JTree) source;
				DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();
				TreePath path = tree.getSelectionPath();
				Object srcData = data.getTransferData(pageFlavor);

				if(path != null && srcData instanceof DefaultMutableTreeNode)
				{
					DefaultMutableTreeNode src = (DefaultMutableTreeNode) srcData;
					DefaultMutableTreeNode dest = (DefaultMutableTreeNode) path.getLastPathComponent();

					PageItem srcItem = (PageItem) src.getUserObject();
					PageItem destItem = (PageItem) dest.getUserObject();

					// move node
					if(dest.isLeaf())
					{
						DefaultMutableTreeNode parent = (DefaultMutableTreeNode) dest.getParent();

						if(parent != null)
						{
							int sort = parent.getIndex(dest);

							moveNode(srcItem.getId(), sort);

							tm.removeNodeFromParent(src);
							tm.insertNodeInto(src, parent, sort);

							logger.info("Set page sort " + srcItem.getId() + " to " + sort);
						}
					}
					else
					{
						reparentNode(srcItem.getId(), destItem.getId());

						tm.removeNodeFromParent(src);
						tm.insertNodeInto(src, dest, dest.getChildCount());

						logger.info("Set parent for page " + srcItem.getId() + " to " + destItem.getId());
					}
				}

				return true;
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());

				return false;
			}
		}

		public boolean canImport(TransferSupport support)
		{
			if(isBusy)
			{
				return false;
			}

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
			if(isBusy)
			{
				return null;
			}

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
