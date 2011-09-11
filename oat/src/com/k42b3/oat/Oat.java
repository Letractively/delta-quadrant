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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.k42b3.oat.http.Http;
import com.k42b3.oat.http.Request;

/**
 * Oat
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Oat extends JFrame
{
	public static String VERSION = "0.0.4 beta";
	
	private JTextField url;
	private JTabbedPane tp;
	private Toolbar toolbar;
	private JList list;

	private ArrayList<RequestFilterInterface> filtersIn = new ArrayList<RequestFilterInterface>();
	private ArrayList<ResponseFilterInterface> filtersOut = new ArrayList<ResponseFilterInterface>();
	private boolean isActive = false;

	private Logger logger;
	private Dig digWin;
	private Form formWin;

	public Oat()
	{
		logger = Logger.getLogger("com.k42b3.oat");

		// settings
		this.setTitle("oat " + VERSION);
		this.setLocation(100, 100);
		this.setSize(600, 500);
		this.setMinimumSize(this.getSize());
		this.setLayout(new BorderLayout());


		// url
		JPanel panelUrl = new JPanel();
		panelUrl.setBorder(BorderFactory.createEmptyBorder(2, 2, 4, 2));
		panelUrl.setLayout(new BorderLayout());

		this.url = new Url();
		this.url.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) 
			{
			}

			public void keyReleased(KeyEvent e) 
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					run();
				}
			}

			public void keyPressed(KeyEvent e) 
			{
			}

		});

		panelUrl.add(this.url, BorderLayout.CENTER);

		this.add(panelUrl, BorderLayout.NORTH);


		// tabbed pane
		this.tp = new JTabbedPane();

		this.add(this.tp, BorderLayout.CENTER);


		// toolbar
		this.toolbar = new Toolbar();
		this.toolbar.getRun().addActionListener(new RunHandler());
		this.toolbar.getNewTab().addActionListener(new NewTabHandler());
		this.toolbar.getSave().addActionListener(new SaveHandler());
		this.toolbar.getReset().addActionListener(new ResetHandler());
		this.toolbar.getDig().addActionListener(new DigHandler());
		this.toolbar.getForm().addActionListener(new FormHandler());
		this.toolbar.getAbout().addActionListener(new AboutHandler());
		this.toolbar.getExit().addActionListener(new ExitHandler());

		this.add(this.toolbar, BorderLayout.SOUTH);


		// list
		this.list = new JList();
		this.list.setModel(new FileList());
		this.list.addListSelectionListener(new ListHandler());
		this.list.setPreferredSize(new Dimension(128, 400));

		JScrollPane scrList = new JScrollPane(this.list);
		scrList.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
		scrList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.add(scrList, BorderLayout.EAST);


		// add new tab
		this.buildNewTab();


		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void saveFile()
	{
		try
		{
			// build xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			Element root = doc.createElement("oat");

			Element uri = doc.createElement("uri");
			uri.setTextContent(this.url.getText());

			Element request = doc.createElement("request");
			request.setTextContent(this.getActiveIn().getText());

			root.appendChild(uri);
			root.appendChild(request);

			doc.appendChild(root);


			// write to file
			Source source = new DOMSource(doc);

			String rawUrl = this.url.getText();

			if(!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://"))
			{
				rawUrl = "http://" + rawUrl;
			}

			URL url = new URL(rawUrl);
			String hash = DigestUtils.md5Hex(this.getActiveIn().getText()).substring(0, 8);
			String fileName = url.getHost() + "_" + hash + ".xml";

			File file = new File(fileName);

			Result result = new StreamResult(file);

			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);


			// reload list
			((FileList) list.getModel()).load();
		}
		catch(Exception e)
		{
			getActiveOut().setText(e.getMessage());
		}
	}

	private void run()
	{
		if(!this.isActive)
		{
			this.isActive = true;

			SwingUtilities.invokeLater(new Runnable(){

				public void run() 
				{
					toolbar.getRun().setEnabled(false);
				}

			});

			try
			{
				Request request = new Request(url.getText(), getActiveIn().getText());

				Http http = new Http(url.getText(), request, new CallbackInterface(){

					public void response(Object content) 
					{
						getActiveOut().setText(content.toString());

						isActive = false;

						SwingUtilities.invokeLater(new Runnable(){

							public void run() 
							{
								toolbar.getRun().setEnabled(true);
							}

						});
					}

				});


				// add response filters
				for(int i = 0; i < filtersOut.size(); i++)
				{
					http.addResponseFilter(filtersOut.get(i));
				}


				// apply request filter 
				for(int i = 0; i < filtersIn.size(); i++)
				{
					filtersIn.get(i).exec(request);
				}


				// start thread
				Thread thread = new Thread(http);
				thread.start();


				getActiveOut().setText("");

				getActiveIn().setText(request.toString());
			}
			catch(Exception ex)
			{
				getActiveOut().setText(ex.getMessage());
			}
		}
	}

	public void buildNewTab()
	{
		// main panel
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sp.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));


		// in
		JPanel panelIn = new JPanel();
		panelIn.setLayout(new BorderLayout());	


		// header
		JPanel panelInHeader = new JPanel();
		panelInHeader.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
		panelInHeader.setLayout(new BorderLayout());

		JLabel lblIn = new JLabel("Request:");
		panelInHeader.add(lblIn, BorderLayout.CENTER);

		JPanel panelBtnFilterIn = new JPanel();
		panelBtnFilterIn.setLayout(new FlowLayout());

		JButton btnInFilter = new JButton("Filter");
		btnInFilter.addActionListener(new InFilterHandler());

		panelBtnFilterIn.add(btnInFilter);
		panelInHeader.add(panelBtnFilterIn, BorderLayout.EAST);

		panelIn.add(panelInHeader, BorderLayout.NORTH);
		
		
		// in textarea
		In in  = new In();
		
		JScrollPane scrIn = new JScrollPane(in);
		scrIn.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
		scrIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrIn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		panelIn.add(scrIn, BorderLayout.CENTER);


		// out
		JPanel panelOut = new JPanel();
		panelOut.setLayout(new BorderLayout());


		// header
		JPanel panelOutHeader = new JPanel();
		panelOutHeader.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
		panelOutHeader.setLayout(new BorderLayout());


		JLabel lblOut = new JLabel("Response:");
		panelOutHeader.add(lblOut, BorderLayout.CENTER);

		JPanel panelBtnFilterOut = new JPanel();
		panelBtnFilterOut.setLayout(new FlowLayout());

		JButton btnOutFilter = new JButton("Filter");
		btnOutFilter.addActionListener(new OutFilterHandler());

		panelBtnFilterOut.add(btnOutFilter);
		panelOutHeader.add(panelBtnFilterOut, BorderLayout.EAST);

		panelOut.add(panelOutHeader, BorderLayout.NORTH);


		// out textarea
		Out out = new Out();

		JScrollPane scrOut = new JScrollPane(out);
		scrOut.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
		scrOut.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrOut.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		panelOut.add(scrOut, BorderLayout.CENTER);


		sp.add(panelIn);
		sp.add(panelOut);


		this.tp.addTab("Request #" + this.tp.getTabCount(), sp);
		this.tp.setSelectedIndex(this.tp.getTabCount() - 1);

		reset();
	}

	private void reset()
	{
		getActiveIn().setText("");
		getActiveOut().setText("");

		url.setText("");
		url.requestFocusInWindow();
	}

	private In getActiveIn()
	{
		JSplitPane sp = (JSplitPane) this.tp.getSelectedComponent();
		JPanel pa = (JPanel) sp.getComponent(1);
		JScrollPane cp = (JScrollPane) pa.getComponent(1);
		JViewport vp = (JViewport) cp.getComponent(0);
		In in = (In) vp.getComponent(0);

		return in;
	}

	private Out getActiveOut()
	{
		JSplitPane sp = (JSplitPane) this.tp.getSelectedComponent();
		JPanel pa = (JPanel) sp.getComponent(2);
		JScrollPane cp = (JScrollPane) pa.getComponent(1);
		JViewport vp = (JViewport) cp.getComponent(0);
		Out out = (Out) vp.getComponent(0);

		return out;
	}

	public static void handleException(Exception e)
	{
		e.printStackTrace();
	}

	public class RunHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			run();
		}
	}

	public class NewTabHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			buildNewTab();
		}
	}

	public class ResetHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			reset();
		}
	}

	public class DigHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(digWin == null)
			{
				digWin = new Dig();
				digWin.pack();
			}

			if(!digWin.isVisible())
			{
				digWin.setVisible(true);
			}

			digWin.requestFocus();
		}
	}

	public class FormHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(formWin == null)
			{
				formWin = new Form();
				formWin.pack();
			}

			if(!formWin.isVisible())
			{
				formWin.setVisible(true);
			}

			formWin.parseHtml(getActiveOut().getText());
			formWin.setCallback(new CallbackInterface() {

				public void response(Object content) 
				{
					getActiveIn().setBody(content.toString());
				}

			});
			formWin.requestFocus();
		}
	}

	public class SaveHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				saveFile();
			}
			catch(Exception ex)
			{
				getActiveOut().setText(ex.getMessage());
			}
		}
	}
	
	public class AboutHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			Out out = getActiveOut();

			out.setText("");
			out.append("Version: oat " + VERSION + "\n");
			out.append("Author: Christoph \"k42b3\" Kappestein" + "\n");
			out.append("Website: http://code.google.com/p/delta-quadrant" + "\n");
			out.append("License: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
			out.append("\n");
			out.append("An application with that you can make raw HTTP requests to any URL. You can" + "\n");
			out.append("save a request for later use. You can apply on the request and the response" + "\n");
			out.append("filters wich can modify the content. The application uses the java nio library" + "\n");
			out.append("to make non-blocking requests so it should work fluently." + "\n");
		}
	}
	
	public class ExitHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			System.exit(0);
		}
	}
	
	public class ListHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e) 
		{
			try
			{
				File file = new File(list.getSelectedValue().toString());

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(file);

				Element rootElement = (Element) doc.getDocumentElement();

				rootElement.normalize();
				
				
				NodeList uriList = doc.getElementsByTagName("uri");
				NodeList requestList = doc.getElementsByTagName("request");
				
				if(uriList.getLength() > 0 && requestList.getLength() > 0)
				{
					url.setText(uriList.item(0).getTextContent());
					getActiveIn().setText(requestList.item(0).getTextContent());
				}
				else
				{
					throw new Exception("uri or request element not found");
				}
			}
			catch(Exception ex)
			{
				getActiveOut().setText(ex.getMessage());
			}
		}
	}

	public class InFilterHandler implements ActionListener
	{
		private FilterIn win;
		
		public void actionPerformed(ActionEvent e) 
		{
			if(!FilterIn.active)
			{
				SwingUtilities.invokeLater(new Runnable(){
					
					public void run() 
					{
						if(win == null)
						{
							win = new FilterIn(new CallbackInterface(){

								public void response(Object content) 
								{
									filtersIn = (ArrayList<RequestFilterInterface>) content;
								}

							});
						}

						win.pack();
						win.setVisible(true);		
					}
					
				});
			}
		}
	}
	
	public class OutFilterHandler implements ActionListener
	{
		private FilterOut win;
		
		public void actionPerformed(ActionEvent e) 
		{
			if(!FilterOut.active)
			{
				SwingUtilities.invokeLater(new Runnable(){
					
					public void run() 
					{
						if(win == null)
						{
							win = new FilterOut(new CallbackInterface(){

								public void response(Object content) 
								{
									filtersOut = (ArrayList<ResponseFilterInterface>) content;
								}

							});
						}

						win.pack();
						win.setVisible(true);			
					}
					
				});
			}
		}
	}
}
