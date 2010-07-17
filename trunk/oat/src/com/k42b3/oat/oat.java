/**
 * oat
 * 
 * An application with that you can make raw http requests to any url. You can 
 * save a request for later use. The application uses the java nio library to 
 * make non-blocking requests so the requests should work fluently.
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of tajet. tajet is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * tajet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with tajet. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.oat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.k42b3.oat.http.http;
import com.k42b3.oat.http.icallback;
import com.k42b3.oat.http.request;

public class oat extends JFrame
{
	public static String ver = "0.0.2 beta";
	
	private JTextField url;
	private in in;
	private out out;
	private toolbar toolbar;
	private JList list;
	
	public oat()
	{
		this.setTitle("oat (version: " + ver + ")");
		
		this.setLocation(100, 100);
		
		this.setSize(600, 500);
		
		this.setMinimumSize(this.getSize());


		// arguments
		this.url = new url();

		this.add(this.url, BorderLayout.NORTH);
		
		
		// main panel
		JPanel panel = new JPanel();
		
		panel.setLayout(new GridLayout(0, 1));
		
		this.in  = new in();
		this.out = new out();
			
		JScrollPane scr_in = new JScrollPane(this.in);
		
		scr_in.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scr_in.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		JScrollPane scr_out = new JScrollPane(this.out);
		
		scr_out.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scr_out.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		panel.add(scr_in);
		panel.add(scr_out);
		
		this.add(panel, BorderLayout.CENTER);
		
		
		// toolbar
		this.toolbar = new toolbar();
		
		this.toolbar.get_run().addActionListener(new run_handler());
		this.toolbar.get_save().addActionListener(new save_handler());
		this.toolbar.get_reset().addActionListener(new reset_handler());
		this.toolbar.get_about().addActionListener(new about_handler());
		this.toolbar.get_exit().addActionListener(new exit_handler());

		this.add(this.toolbar, BorderLayout.SOUTH);


		// list
		this.list = new JList();

		this.list.setModel(new list());

		this.list.addListSelectionListener(new list_handler());

		this.list.setPreferredSize(new Dimension(128, 400));

		JScrollPane scr_list = new JScrollPane(this.list);
		
		scr_list.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scr_list.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.add(scr_list, BorderLayout.EAST);


		this.setVisible(true);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void save_file()
	{
		FileOutputStream file_out;

		try
		{
			String raw_name = this.url.getText();
			StringBuilder name = new StringBuilder();

			if(raw_name.startsWith("http://"))
			{
				raw_name = raw_name.substring(7);
			}

			if(raw_name.startsWith("https://"))
			{
				raw_name = raw_name.substring(8);
			}

			for(int i = 0; i < raw_name.length(); i++)
			{
				if(Character.isLetter(raw_name.charAt(i)) || Character.isDigit(raw_name.charAt(i)))
				{
					name.append(raw_name.charAt(i));
				}
				else if(raw_name.charAt(i) == '.' || raw_name.charAt(i) == '/')
				{
					name.append('_');
				}
			}


			if(name.toString().length() > 3)
			{
				String file = name.toString() + ".oat";
				
				file_out = new FileOutputStream(file);

			    new PrintStream(file_out).print(url.getText() + "\0" + in.getText());

			    file_out.close();	
			}
			
			
			((list) list.getModel()).load();
		}
		catch(IOException e)
		{
			out.setText(e.getMessage());
		}
	}
	
	public class reset_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			url.setText("");
			in.setText("");
			out.setText("");
		}
	}

	public class run_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent args)
		{
			try
			{
				request request = new request(url.getText(), in.getText());

				new Thread(new http(url.getText(), request, new icallback(){
					
					public void response(String content) 
					{
						out.setText(content);
					}

				})).start();

				out.setText("");
				in.setText(request.toString());
			}
			catch(Exception e)
			{
				out.setText(e.getStackTrace().toString());
			}
		}
	}
	
	public class save_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent args)
		{
			try
			{
				save_file();
			}
			catch(Exception e)
			{
			}
		}
	}
	
	public class about_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent args) 
		{
			out.setText("");
			
			out.append("oat version " + ver + "\n");
			out.append("author: Christoph \"k42b3\" Kappestein" + "\n");
			out.append("website: http://code.google.com/p/delta-quadrant" + "\n");
			out.append("license: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
			out.append("\n");
			out.append("An application with that you can make raw http requests to any url. You can" + "\n");
			out.append("save a request for later use. The application uses the java nio library to" + "\n");
			out.append("make non-blocking requests so the requests should work fluently." + "\n");
			out.append("\n");
			out.append("best regards\n");
			out.append("k42b3\n");
		}
	}
	
	public class exit_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent args) 
		{
			System.exit(0);
		}
	}
	
	public class list_handler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e) 
		{
			String file = list.getSelectedValue().toString();
			
			File f_in = new File(file);
			FileInputStream file_in;

			url.setText("");
			in.setText("");
			out.setText("");

			if(f_in.exists())
			{
				try
				{
					file_in = new FileInputStream(file);
					
					BufferedReader br_in = new BufferedReader(new InputStreamReader(file_in));
					
					StringBuilder buffer = new StringBuilder();
					char[] buf = new char[512];
					
					while(br_in.read(buf) > 0)
					{
						buffer.append(buf);
					}

					br_in.close();
					
					String content = buffer.toString();
					
					
					// check for null byte content
					int pos = content.indexOf("\0");
					
					if(pos != -1)
					{
						String txt_url = content.substring(0, pos);
						String txt_in = content.substring(pos).trim();
						
						if(!txt_url.isEmpty() && !txt_in.isEmpty())
						{
							url.setText(txt_url);
							in.setText(txt_in);
						}
					}
					else
					{
						throw new Exception("Invalid file format");
					}
				}
				catch(Exception ex)
				{
					out.setText(ex.getMessage());
				}
			}
		}
	}
}
