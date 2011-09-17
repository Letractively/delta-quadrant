/**
 * oat
 * 
 * An application to send raw http requests to any host. It is designed to
 * debug and test web applications. You can apply filters to the request and
 * response wich can modify the content.
 * 
 * Copyright (c) 2010, 2011 Christoph Kappestein <k42b3.x@gmail.com>
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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.k42b3.oat.filter.CallbackInterface;
import com.k42b3.oat.filter.FilterIn;
import com.k42b3.oat.filter.FilterOut;
import com.k42b3.oat.filter.RequestFilterAbstract;
import com.k42b3.oat.filter.ResponseFilterAbstract;
import com.k42b3.oat.formatter.Formatter;
import com.k42b3.oat.formatter.FormatterInterface;
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
	public static String VERSION = "0.0.5 beta";

	private JTextField url;
	private JTabbedPane tp;
	private JList list;

	private HashMap<Integer, ArrayList<RequestFilterAbstract>> filtersIn;
	private HashMap<Integer, ArrayList<ResponseFilterAbstract>> filtersOut;

	private boolean isActive = false;

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	private Dig digWin;
	private Form formWin;
	private Log logWin;

	private Logger logger = Logger.getLogger("com.k42b3.oat");
	
	public Oat()
	{
		// settings
		this.setTitle("oat " + VERSION);
		this.setLocation(100, 100);
		this.setSize(600, 500);
		this.setMinimumSize(this.getSize());
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		// filter
		filtersIn = new HashMap<Integer, ArrayList<RequestFilterAbstract>>();
		filtersOut = new HashMap<Integer, ArrayList<ResponseFilterAbstract>>();


		// logging handler
		logger.addHandler(new Handler(){

			public void close() throws SecurityException 
			{
			}

			public void flush() 
			{
			}

			public void publish(LogRecord rec)
			{
				logWin.append(rec);
			}

		});

		logWin = new Log();
		logWin.pack();


		// menu
		this.setJMenuBar(this.buildMenuBar());


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
					run(url.getText());
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


		// add new tab
		this.newTab();
	}

	public void run(String url)
	{
		if(!this.isActive)
		{
			this.isActive = true;

			try
			{
				Request request = new Request(url, getActiveIn().getText());

				Http http = new Http(url, request, new CallbackInterface() {

					public void response(Object content) 
					{
						getActiveOut().setText(content.toString());

						isActive = false;
					}

				});


				// add response filters
				if(this.filtersOut.containsKey(this.getSelectedIndex()))
				{
					ArrayList<ResponseFilterAbstract> filtersOut = this.filtersOut.get(this.getSelectedIndex());

					for(int i = 0; i < filtersOut.size(); i++)
					{
						http.addResponseFilter(filtersOut.get(i));
					}
				}


				// apply request filter 
				if(this.filtersIn.containsKey(this.getSelectedIndex()))
				{
					ArrayList<RequestFilterAbstract> filtersIn = this.filtersIn.get(this.getSelectedIndex());

					for(int i = 0; i < filtersIn.size(); i++)
					{
						filtersIn.get(i).exec(request);
					}
				}


				// start thread
				executor.execute(http);


				getActiveOut().setText("");

				getActiveIn().setText(request.toString());
			}
			catch(Exception e)
			{
				Oat.handleException(e);
			}
		}
	}

	public void reset()
	{
		getActiveIn().setText("");
		getActiveOut().setText("");

		url.setText("");

		SwingUtilities.invokeLater(new Runnable() {

			public void run() 
			{
				url.requestFocusInWindow();
			}

		});
	}

	public void newTab()
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


		// load default filters
		loadDefaultFilters();


		reset();
	}

	public void closeTab()
	{
		if(this.tp.getTabCount() > 1)
		{
			this.tp.remove(this.tp.getSelectedIndex());
		}
	}

	public void save(File file)
	{
		try
		{
			// check file extension
			if(!file.getName().endsWith(".xml"))
			{
				file = new File(file.getAbsolutePath() + ".xml");
			}


			// build xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			doc.normalizeDocument();
			
			Element root = doc.createElement("oat");

			Element uri = doc.createElement("uri");
			uri.setTextContent(this.url.getText());

			Element request = doc.createElement("request");
			request.appendChild(doc.createCDATASection(this.getActiveIn().getText()));

			Element filters = doc.createElement("filters");


			// in filters
			if(this.filtersIn.containsKey(this.getSelectedIndex()))
			{
				Element in = doc.createElement("in");

				ArrayList<RequestFilterAbstract> filtersIn = this.filtersIn.get(this.getSelectedIndex());
				
				for(int i = 0; i < filtersIn.size(); i++)
				{
					Element filter = doc.createElement("filter");
					filter.setAttribute("name", filtersIn.get(i).getName());

					Properties config = filtersIn.get(i).getConfig();

					if(config != null)
					{
						Set set = config.entrySet();
						Iterator iter = set.iterator();

						while(iter.hasNext())
						{
							Map.Entry me = (Map.Entry) iter.next();

							Element property = doc.createElement("property");
							property.setAttribute("name", me.getKey().toString());
							property.setTextContent(me.getValue().toString());

							filter.appendChild(property);
						}
					}

					in.appendChild(filter);
				}

				filters.appendChild(in);
			}


			// out filters
			if(this.filtersOut.containsKey(this.getSelectedIndex()))
			{
				Element out = doc.createElement("out");

				ArrayList<ResponseFilterAbstract> filtersIn = this.filtersOut.get(this.getSelectedIndex());

				for(int i = 0; i < filtersIn.size(); i++)
				{
					Element filter = doc.createElement("filter");
					filter.setAttribute("name", filtersIn.get(i).getName());

					Properties config = filtersIn.get(i).getConfig();

					if(config != null)
					{
						Set set = config.entrySet();
						Iterator iter = set.iterator();

						while(iter.hasNext())
						{
							Map.Entry me = (Map.Entry) iter.next();

							Element property = doc.createElement("property");
							property.setAttribute("name", me.getKey().toString());
							property.setTextContent(me.getValue().toString());

							filter.appendChild(property);
						}
					}

					out.appendChild(filter);
				}

				filters.appendChild(out);
			}


			root.appendChild(uri);
			root.appendChild(request);
			root.appendChild(filters);

			doc.appendChild(root);


			// write to file
			Source source = new DOMSource(doc);
			Result result = new StreamResult(file);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);


			logger.info("Saved successful to " + file.getAbsolutePath());
		}
		catch(Exception e)
		{
			Oat.handleException(e);
		}
	}
	
	public void open(File file)
	{
		try
		{
			// read xml
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
				throw new Exception("Uri or request element not found");
			}


			// filter
			NodeList filtersList = doc.getElementsByTagName("filter");

			parseFilters(filtersList);


			logger.info("Loaded successful from " + file.getAbsolutePath());
		}
		catch(Exception e)
		{
			Oat.handleException(e);
		}
	}

	public void dig()
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run()
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

		});
	}

	public void form()
	{
		SwingUtilities.invokeLater(new Runnable() {

			public void run()
			{
				if(formWin != null)
				{
					formWin.setVisible(false);
				}

				formWin = new Form();
				formWin.pack();
				formWin.setVisible(true);
				
				formWin.parseHtml(getActiveOut().getText());
				formWin.setCallback(new CallbackInterface() {

					public void response(Object content) 
					{
						getActiveIn().setBody(content.toString());
					}

				});
			}

		});
	}

	public void format(int type)
	{
		try
		{
			FormatterInterface formatter = Formatter.factory(type);

			getActiveOut().setBody(formatter.format(getActiveOut().getBody()));
		}
		catch(Exception e)
		{
			Oat.handleException(e);
		}
	}

	public void log()
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run()
			{
				if(!logWin.isVisible())
				{
					logWin.setVisible(true);
				}

				logWin.requestFocus();
			}

		});
	}

	public void about()
	{
		StringBuilder out = new StringBuilder();

		out.append("Version: oat " + VERSION + "\n");
		out.append("Author: Christoph \"k42b3\" Kappestein" + "\n");
		out.append("Website: http://code.google.com/p/delta-quadrant" + "\n");
		out.append("License: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
		out.append("\n");
		out.append("An application to send raw http requests to any host. It is designed to" + "\n");
		out.append("debug and test web applications. You can apply filters to the request and" + "\n");
		out.append("response wich can modify the content." + "\n");

		JOptionPane.showMessageDialog(this, out, "About", JOptionPane.INFORMATION_MESSAGE);
	}

	public In getActiveIn()
	{
		JSplitPane sp = (JSplitPane) this.tp.getSelectedComponent();
		JPanel pa = (JPanel) sp.getComponent(1);
		JScrollPane cp = (JScrollPane) pa.getComponent(1);
		JViewport vp = (JViewport) cp.getComponent(0);
		In in = (In) vp.getComponent(0);

		return in;
	}

	public Out getActiveOut()
	{
		JSplitPane sp = (JSplitPane) this.tp.getSelectedComponent();
		JPanel pa = (JPanel) sp.getComponent(2);
		JScrollPane cp = (JScrollPane) pa.getComponent(1);
		JViewport vp = (JViewport) cp.getComponent(0);
		Out out = (Out) vp.getComponent(0);

		return out;
	}

	private int getSelectedIndex()
	{
		return this.tp.getSelectedIndex();
	}

	private Oat getSelf()
	{
		return this;
	}

	private void loadDefaultFilters()
	{
		try
		{
			// read xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(Oat.getConfig());

			Element rootElement = (Element) doc.getDocumentElement();

			rootElement.normalize();


			// filter
			NodeList filtersList = doc.getElementsByTagName("filter");

			parseFilters(filtersList);
		}
		catch(Exception e)
		{
			Oat.handleException(e);
		}
	}

	private void parseFilters(NodeList filtersList)
	{
		ArrayList<RequestFilterAbstract> filtersIn = new ArrayList<RequestFilterAbstract>();
		ArrayList<ResponseFilterAbstract> filtersOut = new ArrayList<ResponseFilterAbstract>();

		for(int i = 0; i < filtersList.getLength(); i++)
		{
			try
			{
				Element filterElement = (Element) filtersList.item(i);
				
				// in
				if(filterElement.getParentNode().getNodeName().equals("in"))
				{
					String cls = filterElement.getAttribute("name");
					Properties config = new Properties();

					NodeList propertyList = filterElement.getElementsByTagName("property");

					for(int j = 0; j < propertyList.getLength(); j++)
					{
						Element property = (Element) propertyList.item(j);

						config.put(property.getAttribute("name"), property.getTextContent());
					}

					Class c = Class.forName(cls);

					RequestFilterAbstract filter = (RequestFilterAbstract) c.newInstance();
					filter.setConfig(config);
					filter.setInstance(this);

					filtersIn.add(filter);
				}
				// out
				else if(filterElement.getParentNode().getNodeName().equals("out"))
				{
					String cls = filterElement.getAttribute("name");
					Properties config = new Properties();

					NodeList propertyList = filterElement.getElementsByTagName("property");

					for(int j = 0; j < propertyList.getLength(); j++)
					{
						Element property = (Element) propertyList.item(j);

						config.put(property.getAttribute("name"), property.getTextContent());
					}

					Class c = Class.forName(cls);

					ResponseFilterAbstract filter = (ResponseFilterAbstract) c.newInstance();
					filter.setConfig(config);
					filter.setInstance(this);

					filtersOut.add(filter);
				}
			}
			catch(Exception e)
			{
				Oat.handleException(e);
			}
		}

		if(filtersIn.size() > 0)
		{
			logger.info("Loaded " + filtersIn.size() + " request filter");

			this.filtersIn.put(getSelectedIndex(), filtersIn);
		}

		if(filtersOut.size() > 0)
		{
			logger.info("Loaded " + filtersOut.size() + " response filter");

			this.filtersOut.put(getSelectedIndex(), filtersOut);
		}
	}

	private JMenuBar buildMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();


		// url
		JMenu menuUrl = new JMenu("URL");

		JMenuItem itemRun = new JMenuItem("Run");
		itemRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
		itemRun.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				run(url.getText());
			}

		});
		menuUrl.add(itemRun);

		JMenuItem itemReset = new JMenuItem("Reset");
		itemReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		itemReset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				reset();
			}

		});
		menuUrl.add(itemReset);

		JMenuItem itemNewTab = new JMenuItem("New Tab");
		itemNewTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
		itemNewTab.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				newTab();
			}

		});
		menuUrl.add(itemNewTab);

		JMenuItem itemCloseTab = new JMenuItem("Close Tab");
		itemCloseTab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		itemCloseTab.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				closeTab();
			}

		});
		menuUrl.add(itemCloseTab);

		JMenuItem itemSave = new JMenuItem("Save");
		itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		itemSave.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				// save file
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new XmlFilter());

				int returnVal = fc.showSaveDialog(Oat.this);

				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					save(fc.getSelectedFile());
				}
			}

		});
		menuUrl.add(itemSave);

		JMenuItem itemLoad = new JMenuItem("Open");
		itemLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
		itemLoad.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				// load file
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new XmlFilter());

				int returnVal = fc.showOpenDialog(Oat.this);

				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					open(fc.getSelectedFile());
				}
			}

		});
		menuUrl.add(itemLoad);

		menuBar.add(menuUrl);

		
		// extras
		JMenu menuExtras = new JMenu("Extras");
		
		JMenuItem itemDig = new JMenuItem("Dig");
		itemDig.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK));
		itemDig.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				dig();
			}

		});
		menuExtras.add(itemDig);
		
		JMenuItem itemForm = new JMenuItem("Form");
		itemForm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.ALT_MASK));
		itemForm.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				form();
			}

		});
		menuExtras.add(itemForm);

		menuBar.add(menuExtras);


		// format
		JMenu menuFormat = new JMenu("Format");

		JMenuItem itemXml = new JMenuItem("XML");
		itemXml.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
		itemXml.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				format(Formatter.XML);
			}

		});
		menuFormat.add(itemXml);
		
		JMenuItem itemJson = new JMenuItem("JSON");
		itemJson.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.ALT_MASK));
		itemJson.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				format(Formatter.JSON);
			}

		});
		menuFormat.add(itemJson);

		menuBar.add(menuFormat);


		// help
		JMenu menuHelp = new JMenu("Help");

		JMenuItem itemLog = new JMenuItem("Log");
		itemLog.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
		itemLog.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				log();
			}

		});
		menuHelp.add(itemLog);

		JMenuItem itemAbout = new JMenuItem("About");
		itemAbout.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) 
			{
				about();
			}

		});
		menuHelp.add(itemAbout);

		menuBar.add(menuHelp);

		return menuBar;
	}

	public static void handleException(Exception e)
	{
		Logger.getLogger("com.k42b3.oat").log(Level.WARNING, e.getMessage());

		e.printStackTrace();
	}

	public static File getConfig()
	{
		return new File("oat.conf.xml");
	}

	public class InFilterHandler implements ActionListener
	{
		private FilterIn filterWin;

		public void actionPerformed(ActionEvent e) 
		{
			SwingUtilities.invokeLater(new Runnable(){

				public void run() 
				{
					if(filterWin == null)
					{
						if(!filtersIn.containsKey(getSelectedIndex()))
						{
							filtersIn.put(getSelectedIndex(), new ArrayList<RequestFilterAbstract>());
						}

						filterWin = new FilterIn(filtersIn.get(getSelectedIndex()), getSelf());
					}

					filterWin.pack();
					filterWin.setVisible(true);		
				}

			});
		}
	}

	public class OutFilterHandler implements ActionListener
	{
		private FilterOut filterWin;
		
		public void actionPerformed(ActionEvent e) 
		{
			SwingUtilities.invokeLater(new Runnable(){

				public void run() 
				{
					if(filterWin == null)
					{
						if(!filtersOut.containsKey(getSelectedIndex()))
						{
							filtersOut.put(getSelectedIndex(), new ArrayList<ResponseFilterAbstract>());
						}

						filterWin = new FilterOut(filtersOut.get(getSelectedIndex()), getSelf());
					}

					filterWin.pack();
					filterWin.setVisible(true);			
				}

			});
		}
	}

	public class XmlFilter extends FileFilter
	{
		public boolean accept(File file)
		{
			if(file.isFile())
			{
				int pos = file.getName().lastIndexOf('.');

				if(pos != -1 && file.getName().length() > pos + 1)
				{
					return file.getName().substring(pos + 1).toLowerCase().equals("xml");
				}
			}

			return false;
	    }

		public String getDescription() 
		{
			return "XML File";
		}
	}
}
