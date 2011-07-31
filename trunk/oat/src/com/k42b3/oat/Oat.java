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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JTextField;
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
 * oat
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Oat extends JFrame
{
	public static String ver = "0.0.4 beta";
	
	private JTextField url;
	private In in;
	private Out out;
	private Toolbar toolbar;
	private JList list;
	
	private ArrayList<RequestFilterInterface> filtersIn = new ArrayList<RequestFilterInterface>();
	private ArrayList<ResponseFilterInterface> filtersOut = new ArrayList<ResponseFilterInterface>();

	private Logger logger;

	public Oat()
	{
		logger = Logger.getLogger("com.k42b3.oat");


		this.setTitle("oat (version: " + ver + ")");
		
		this.setLocation(100, 100);
		
		this.setSize(600, 500);
		
		this.setMinimumSize(this.getSize());

		this.setLayout(new BorderLayout());


		// url
		JPanel panelUrl = new JPanel();
		
		panelUrl.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		panelUrl.setLayout(new BorderLayout());
				
		
		this.url = new Url();
		
		panelUrl.add(this.url, BorderLayout.CENTER);


		this.add(panelUrl, BorderLayout.NORTH);


		// main panel
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		
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
		
		btnInFilter.addActionListener(new inFilterHandler());
		
		panelBtnFilterIn.add(btnInFilter);
		
		panelInHeader.add(panelBtnFilterIn, BorderLayout.EAST);
		
		
		panelIn.add(panelInHeader, BorderLayout.NORTH);
		
		
		
		this.in  = new In();
		
		JScrollPane scrIn = new JScrollPane(this.in);

		scrIn.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
		
		scrIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		scrIn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
		
		panelIn.add(scrIn, BorderLayout.CENTER);
		
		
		sp.add(panelIn);
		
		
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
		
		btnOutFilter.addActionListener(new outFilterHandler());
		
		panelBtnFilterOut.add(btnOutFilter);
		
		panelOutHeader.add(panelBtnFilterOut, BorderLayout.EAST);
		
		
		panelOut.add(panelOutHeader, BorderLayout.NORTH);
		
		
		this.out = new Out();
			
		JScrollPane scrOut = new JScrollPane(this.out);
		
		scrOut.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
		
		scrOut.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		scrOut.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		panelOut.add(scrOut, BorderLayout.CENTER);


		sp.add(panelOut);


		this.add(sp, BorderLayout.CENTER);


		// toolbar
		this.toolbar = new Toolbar();
		
		this.toolbar.getRun().addActionListener(new runHandler());
		
		this.toolbar.getSave().addActionListener(new saveHandler());
		
		this.toolbar.getReset().addActionListener(new resetHandler());
		
		this.toolbar.getAbout().addActionListener(new aboutHandler());
		
		this.toolbar.getExit().addActionListener(new exitHandler());

		this.add(this.toolbar, BorderLayout.SOUTH);


		// list
		this.list = new JList();

		this.list.setModel(new FileList());

		this.list.addListSelectionListener(new listHandler());

		this.list.setPreferredSize(new Dimension(128, 400));

		JScrollPane scrList = new JScrollPane(this.list);
		
		scrList.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
		
		scrList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		scrList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.add(scrList, BorderLayout.EAST);

		
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
			request.setTextContent(this.in.getText());

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
			String hash = DigestUtils.md5Hex(this.in.getText()).substring(0, 8);
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
			out.setText(e.getMessage());
		}
	}

	public class resetHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			url.setText("");
			in.setText("");
			out.setText("");

			url.requestFocusInWindow();
		}
	}

	public class runHandler implements ActionListener
	{
		private boolean isActive = false;

		public void actionPerformed(ActionEvent e)
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
					Request request = new Request(url.getText(), in.getText());

					Http http = new Http(url.getText(), request, new CallbackInterface(){

						public void response(Object content) 
						{
							out.setText(content.toString());

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
						http.add_response_filter(filtersOut.get(i));
					}


					// apply request filter 
					for(int i = 0; i < filtersIn.size(); i++)
					{
						filtersIn.get(i).exec(request);
					}


					// start thread
					new Thread(new ThreadGroup("http"), http).start();


					out.setText("");

					in.setText(request.toString());
				}
				catch(Exception ex)
				{
					out.setText(ex.getMessage());
				}
			}
		}
	}
	
	public class saveHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				saveFile();
			}
			catch(Exception ex)
			{
				out.setText(ex.getMessage());
			}
		}
	}
	
	public class aboutHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			out.setText("");
			
			out.append("oat version " + ver + "\n");
			out.append("author: Christoph \"k42b3\" Kappestein" + "\n");
			out.append("website: http://code.google.com/p/delta-quadrant" + "\n");
			out.append("license: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
			out.append("\n");
			out.append("An application with that you can make raw http requests to any url. You can" + "\n");
			out.append("save a request for later use. You can apply on the request and the response" + "\n");
			out.append("filters wich can modify the content. The application uses the java nio library" + "\n");
			out.append("to make non-blocking requests so it should work fluently." + "\n");
			out.append("\n");
			out.append("best regards\n");
			out.append("k42b3\n");
		}
	}
	
	public class exitHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			System.exit(0);
		}
	}
	
	public class listHandler implements ListSelectionListener
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
					in.setText(requestList.item(0).getTextContent());
				}
				else
				{
					throw new Exception("uri or request element not found");
				}
			}
			catch(Exception ex)
			{
				out.setText(ex.getMessage());
			}
		}
	}

	public class inFilterHandler implements ActionListener
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
	
	public class outFilterHandler implements ActionListener
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
