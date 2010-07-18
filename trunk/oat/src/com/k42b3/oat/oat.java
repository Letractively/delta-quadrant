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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

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

import com.k42b3.oat.http.http;
import com.k42b3.oat.http.request;
import com.k42b3.oat.model.file_list;

/**
 * oat
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class oat extends JFrame
{
	public static String ver = "0.0.3 beta";
	
	private JTextField url;
	private in in;
	private out out;
	private toolbar toolbar;
	private JList list;
	
	private ArrayList<irequest_filter> filters_in = new ArrayList<irequest_filter>();
	private ArrayList<iresponse_filter> filters_out = new ArrayList<iresponse_filter>();

	public oat()
	{
		this.setTitle("oat (version: " + ver + ")");
		
		this.setLocation(100, 100);
		
		this.setSize(600, 500);
		
		this.setMinimumSize(this.getSize());

		this.setLayout(new BorderLayout());

		
		// url
		JPanel panel_url = new JPanel();
		
		panel_url.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		panel_url.setLayout(new BorderLayout());
				
		
		this.url = new url();
		
		panel_url.add(this.url, BorderLayout.CENTER);


		this.add(panel_url, BorderLayout.NORTH);


		// main panel
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		
		// in
		JPanel panel_in = new JPanel();
		
		panel_in.setLayout(new BorderLayout());
				
		
		// header
		JPanel panel_in_header = new JPanel();
		
		panel_in_header.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
		
		panel_in_header.setLayout(new BorderLayout());
		
		
		JLabel lbl_in = new JLabel("Request:");
		
		panel_in_header.add(lbl_in, BorderLayout.CENTER);
		
		
		JPanel panel_btn_filter_in = new JPanel();
		
		panel_btn_filter_in.setLayout(new FlowLayout());
		
		JButton btn_in_filter = new JButton("Filter");
		
		btn_in_filter.addActionListener(new in_filter_handler());
		
		panel_btn_filter_in.add(btn_in_filter);
		
		panel_in_header.add(panel_btn_filter_in, BorderLayout.EAST);
		
		
		panel_in.add(panel_in_header, BorderLayout.NORTH);
		
		
		
		this.in  = new in();
		
		JScrollPane scr_in = new JScrollPane(this.in);

		scr_in.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
		
		scr_in.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		scr_in.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
		
		panel_in.add(scr_in, BorderLayout.CENTER);
		
		
		sp.add(panel_in);
		
		
		// out
		JPanel panel_out = new JPanel();
		
		panel_out.setLayout(new BorderLayout());
				
		
		// header
		JPanel panel_out_header = new JPanel();
		
		panel_out_header.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
		
		panel_out_header.setLayout(new BorderLayout());
		
		
		JLabel lbl_out = new JLabel("Response:");
		
		panel_out_header.add(lbl_out, BorderLayout.CENTER);
		
		
		JPanel panel_btn_filter_out = new JPanel();
		
		panel_btn_filter_out.setLayout(new FlowLayout());
		
		JButton btn_out_filter = new JButton("Filter");
		
		btn_out_filter.addActionListener(new out_filter_handler());
		
		panel_btn_filter_out.add(btn_out_filter);
		
		panel_out_header.add(panel_btn_filter_out, BorderLayout.EAST);
		
		
		panel_out.add(panel_out_header, BorderLayout.NORTH);
		
		
		this.out = new out();
			
		JScrollPane scr_out = new JScrollPane(this.out);
		
		scr_out.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
		
		scr_out.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		scr_out.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		panel_out.add(scr_out, BorderLayout.CENTER);


		sp.add(panel_out);


		this.add(sp, BorderLayout.CENTER);


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

		this.list.setModel(new file_list());

		this.list.addListSelectionListener(new list_handler());

		this.list.setPreferredSize(new Dimension(128, 400));

		JScrollPane scr_list = new JScrollPane(this.list);
		
		scr_list.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
		
		scr_list.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		scr_list.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.add(scr_list, BorderLayout.EAST);

		
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
			
			
			((file_list) list.getModel()).load();
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

			url.requestFocusInWindow();
		}
	}

	public class run_handler implements ActionListener
	{
		private boolean is_active = false;

		public void actionPerformed(ActionEvent e)
		{
			if(!this.is_active)
			{
				this.is_active = true;

				SwingUtilities.invokeLater(new Runnable(){
					
					public void run() 
					{
						toolbar.get_run().setEnabled(false);
					}

				});

				try
				{
					request request = new request(url.getText(), in.getText());

					http http = new http(url.getText(), request, new icallback(){

						public void response(Object content) 
						{
							out.setText(content.toString());

							is_active = false;
							
							SwingUtilities.invokeLater(new Runnable(){
								
								public void run() 
								{
									toolbar.get_run().setEnabled(true);
								}

							});
						}

					});


					// add response filters
					for(int i = 0; i < filters_out.size(); i++)
					{
						http.add_response_filter(filters_out.get(i));
					}


					// apply request filter 
					for(int i = 0; i < filters_in.size(); i++)
					{
						filters_in.get(i).exec(request);
					}


					// start thread
					new Thread(new ThreadGroup("http"), http).start();


					out.setText("");

					in.setText(request.toString());
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	public class save_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				save_file();
			}
			catch(Exception ex)
			{
				out.setText(ex.getStackTrace().toString());
			}
		}
	}
	
	public class about_handler implements ActionListener
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
			out.append("save a request for later use. The application uses the java nio library to" + "\n");
			out.append("make non-blocking requests so the requests should work fluently." + "\n");
			out.append("\n");
			out.append("best regards\n");
			out.append("k42b3\n");
		}
	}
	
	public class exit_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
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
	
	public class in_filter_handler implements ActionListener
	{
		private filter_in win;
		
		public void actionPerformed(ActionEvent e) 
		{
			if(!filter_in.active)
			{
				SwingUtilities.invokeLater(new Runnable(){
					
					public void run() 
					{
						if(win == null)
						{
							win = new filter_in(new icallback(){

								public void response(Object content) 
								{
									filters_in = (ArrayList<irequest_filter>) content;
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
	
	public class out_filter_handler implements ActionListener
	{
		private filter_out win;
		
		public void actionPerformed(ActionEvent e) 
		{
			if(!filter_out.active)
			{
				SwingUtilities.invokeLater(new Runnable(){
					
					public void run() 
					{
						if(win == null)
						{
							win = new filter_out(new icallback(){

								public void response(Object content) 
								{
									filters_out = (ArrayList<iresponse_filter>) content;
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
