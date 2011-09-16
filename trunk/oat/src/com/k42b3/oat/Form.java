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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.k42b3.oat.filter.CallbackInterface;
import com.k42b3.oat.http.Util;

/**
 * Form
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Form extends JFrame
{
	private CallbackInterface cb;
	private ArrayList<FormData> forms = new ArrayList<FormData>();
	private ArrayList<HashMap<String, JTextField>> fields = new ArrayList<HashMap<String, JTextField>>();
	private JTabbedPane tb;
	
	private Logger logger = Logger.getLogger("com.k42b3.oat");

	public Form()
	{
		// settings
		this.setTitle("oat " + Oat.VERSION);
		this.setLocation(100, 100);
		this.setSize(360, 400);
		this.setMinimumSize(this.getSize());
		this.setResizable(false);
		this.setLayout(new BorderLayout());


		// tab panel
		tb = new JTabbedPane();

		this.add(tb, BorderLayout.CENTER);
		
		
		// buttons
		JPanel panelButtons = new JPanel();

		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);

		panelButtons.setLayout(fl);

		JButton btnInsert = new JButton("Insert");
		btnInsert.setMnemonic(java.awt.event.KeyEvent.VK_I);
		btnInsert.addActionListener(new InsertHandler());

		panelButtons.add(btnInsert);

		this.add(panelButtons, BorderLayout.SOUTH);
	}
	
	public void parseHtml(String html)
	{
		this.reset();

		this.parseForm(html);

		this.buildElements();
	}

	private void reset()
	{
		for(int i = 0; i < this.tb.getTabCount(); i++)
		{
			this.tb.removeTabAt(i);
		}

		this.forms.clear();
	}

	private void insert()
	{
		if(this.fields.size() > 0)
		{
			StringBuilder response = new StringBuilder();
			HashMap<String, JTextField> fields = this.fields.get(this.tb.getSelectedIndex());
			Set<Entry<String, JTextField>> set = fields.entrySet();
			Iterator<Entry<String, JTextField>> iter = set.iterator();

			while(iter.hasNext())
			{
				Map.Entry<String, JTextField> item = (Map.Entry<String, JTextField>) iter.next();
				String value = Util.urlEncode(item.getValue().getText());

				if(value != null)
				{
					response.append(item.getKey() + "=" + value);

					if(iter.hasNext())
					{
						response.append('&');
					}
				}
			}

			this.cb.response(response.toString());
		}

		this.setVisible(false);
	}

	private void buildElements()
	{
		if(forms.size() > 0)
		{
			for(int i = 0; i < forms.size(); i++)
			{
				HashMap<String, JTextField> fields = new HashMap<String, JTextField>();

				Set<Entry<String, String>> set = forms.get(i).getValues().entrySet();
				Iterator<Entry<String, String>> iter = set.iterator();

				JPanel containerPanel = new JPanel();
				containerPanel.setLayout(new FlowLayout());
				containerPanel.setPreferredSize(new Dimension(320, 600));

				JPanel formPanel = new JPanel();
				formPanel.setLayout(new GridLayout(0, 1));

				JPanel panel = new JPanel();
				panel.setLayout(new FlowLayout());
				JTextField txtForm = new JTextField(forms.get(i).getMethod() + " " + forms.get(i).getUrl());
				txtForm.setPreferredSize(new Dimension(305, 20));
				panel.add(txtForm);

				formPanel.add(panel);

				while(iter.hasNext())
				{
					Map.Entry<String, String> item = (Map.Entry<String, String>) iter.next();

					panel = new JPanel();
					panel.setLayout(new FlowLayout());

					JLabel lblName = new JLabel(item.getKey());
					lblName.setPreferredSize(new Dimension(100, 20));
					JTextField txtValue = new JTextField(item.getValue());
					txtValue.setPreferredSize(new Dimension(200, 20));

					fields.put(item.getKey(), txtValue);

					panel.add(lblName);
					panel.add(txtValue);

					formPanel.add(panel);
				}

				containerPanel.add(formPanel);

				JScrollPane scp = new JScrollPane(containerPanel);
				scp.setBorder(new EmptyBorder(4, 4, 4, 4));

				tb.addTab("Form #" + i, scp);

				this.fields.add(fields);
			}
		}
		else
		{
			JPanel containerPanel = new JPanel();
			containerPanel.setLayout(new FlowLayout());
			containerPanel.add(new JLabel("No elements found"));

			tb.addTab("Form #0", containerPanel);
		}
	}

	private void parseForm(String html)
	{
		FormData form = null;

		for(int i = 0; i < html.length(); i++)
		{
			if(form == null)
			{
				if(this.startsWith("<form", i, html))
				{
					logger.info("Found start form tag at " + i);

					String formTag = getTag(i, html);
					String method = this.getAttribute("method", formTag);
					String action = this.getAttribute("action", formTag);

					form = new FormData(method, action);
				}
			}
			else
			{
				if(this.startsWith("<input", i, html))
				{
					logger.info("Found input start tag at " + i);

					String inputTag = getTag(i, html);
					String name = this.getAttribute("name", inputTag);
					String value = this.getAttribute("value", inputTag);

					form.addElement(name, value);
				}

				if(this.startsWith("<textarea", i, html))
				{
					logger.info("Found textarea start tag at " + i);

					String textareaTag = getTag(i, html);
					String name = this.getAttribute("name", textareaTag);

					form.addElement(name, "");
				}

				if(this.startsWith("<select", i, html))
				{
					logger.info("Found select start tag at " + i);

					String selectTag = getTag(i, html);
					String name = this.getAttribute("name", selectTag);

					form.addElement(name, "");
				}

				if(this.startsWith("</form>", i, html))
				{
					logger.info("Found end form tag at " + i);

					forms.add(form);

					form = null;
				}
			}
		}
	}

	private String getAttribute(String name, String content)
	{
		StringBuilder value = new StringBuilder();
		int sPos = -1;
		boolean noWhiteSpaces = false;
		boolean inVal = false;

		for(int i = 0; i < content.length(); i++)
		{
			if(inVal)
			{
				if(content.charAt(i) == '"' || content.charAt(i) == '\'' || content.charAt(i) == '>')
				{
					break;
				}

				if(noWhiteSpaces && Character.isWhitespace(content.charAt(i)))
				{
					break;
				}

				value.append(content.charAt(i));
			}
			else
			{
				if(this.startsWith(name + "=", i, content))
				{
					sPos = i + name.length();

					if(content.charAt(sPos + 1) != '"' && content.charAt(sPos + 1) != '\'')
					{
						noWhiteSpaces = true;
					}
					else
					{
						sPos++;
					}

					logger.info("Found start attribute at pos " + sPos);
				}
			}

			if(i == sPos)
			{
				inVal = true;
			}
		}

		logger.info("Value for attribute " + name + ": " + value.toString());

		return value.toString();
	}

	private String getTag(int index, String content)
	{
		int pos = content.indexOf('>', index);

		if(pos != -1)
		{
			return content.substring(index, pos + 1);
		}

		return null;
	}

	public void setCallback(CallbackInterface cb)
	{
		this.cb = cb;
	}
	
	private boolean startsWith(String phrase, int index, String content)
	{
		for(int i = 0; i < phrase.length(); i++)
		{
			if(Character.toLowerCase(content.charAt(index + i)) != Character.toLowerCase(phrase.charAt(i)))
			{
				return false;
			}
		}

		return true;	
	}

	public class FormData
	{
		private String url;
		private String method;
		private HashMap<String, String> values = new HashMap<String, String>();

		public FormData(String method, String url)
		{
			this.setMethod(method);
			this.setUrl(url);
		}

		public FormData(String method)
		{
			this(method, null);
		}

		public String getUrl() 
		{
			return url;
		}

		public void setUrl(String url) 
		{
			this.url = url;
		}

		public String getMethod() 
		{
			return method;
		}

		public void setMethod(String method) 
		{
			if(method == null || method.isEmpty())
			{
				method = "GET";
			}

			method = method.toUpperCase();

			if(method.equals("GET") || method.equals("POST"))
			{
				this.method = method;
			}
			else
			{
				this.method = "GET";
			}
		}

		public void addElement(String name, String value)
		{
			if(name != null && !name.isEmpty())
			{
				values.put(name, value);
			}
		}

		public HashMap<String, String> getValues() 
		{
			return values;
		}
	}

	public class InsertHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			insert();
		}
	}
}
