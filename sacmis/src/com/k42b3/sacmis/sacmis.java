/**
 * sacmis
 * 
 * An application wich let you write to the stdin of any executable file and
 * return the stdout on success or stderr on error. You have to specify the
 * executable file as first argument and optional as second argument the 
 * expected exit value (default is 0)
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of sacmis. sacmis is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * sacmis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with sacmis. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.sacmis;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * sacmis
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class sacmis extends JFrame
{
	public static String ver = "0.0.5 beta";
	public static String file = "input.cache";

	private String path;
	private int exit_code;
	private long timeout = 4000;
	
	private JTextField args;
	private in in;
	private out out;
	private toolbar toolbar;

	private ByteArrayOutputStream baos;
	private ByteArrayOutputStream baos_err;
	private ByteArrayInputStream bais;
	
	public sacmis(String path, int exit_code) throws Exception
	{
		this.path = path;
		this.exit_code = exit_code;

		
		this.setTitle("sacmis (version: " + ver + ")");
		
		this.setLocation(100, 100);
		
		this.setSize(600, 500);
		
		this.setMinimumSize(this.getSize());

		
		// arguments
		JPanel panel_args = new JPanel();
		
		panel_args.setLayout(new BorderLayout());
		
		panel_args.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		
		this.args = new args();

		panel_args.add(this.args, BorderLayout.CENTER);
		
		
		this.add(panel_args, BorderLayout.NORTH);
		
		
		// main panel
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		
		this.in  = new in();
		
		JScrollPane scr_in = new JScrollPane(this.in);
		
		scr_in.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		scr_in.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		scr_in.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		sp.add(scr_in);
		
		
		this.out = new out();
		
		JScrollPane scr_out = new JScrollPane(this.out);
		
		scr_out.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		scr_out.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		scr_out.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		sp.add(scr_out);
		
				
		this.add(sp, BorderLayout.CENTER);

		
		// toolbar
		this.toolbar = new toolbar();
		
		this.toolbar.get_run().addActionListener(new run_handler());
		this.toolbar.get_reset().addActionListener(new reset_handler());
		this.toolbar.get_about().addActionListener(new about_handler());
		this.toolbar.get_exit().addActionListener(new exit_handler());
		
		this.getContentPane().add(this.toolbar, BorderLayout.SOUTH);
		
		
		this.setVisible(true);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		this.load_file();
	}
	
	private void load_file()
	{
		File f_in = new File(file);
		FileInputStream file_in;
	
		in.setText("");
		out.setText("");
	
		if(f_in.exists())
		{
			try
			{
				file_in = new FileInputStream(file);
				
				BufferedReader br_in = new BufferedReader(new InputStreamReader(file_in));
				
				String line = null;
				
				while((line = br_in.readLine()) != null)
				{
					in.append(line + "\n");
				}
				
				br_in.close();
			}
			catch(IOException e)
			{
				out.setText(e.getMessage());
			}
		}
	}
	
	private void save_file()
	{
		FileOutputStream file_out;
		
		try
		{
			file_out = new FileOutputStream(file);

		    new PrintStream(file_out).print(in.getText());

		    file_out.close();		
		}
		catch(IOException e)
		{
			out.setText(e.getMessage());
		}
	}
	
	private void execute_command(byte[] data)
	{
		out.setText("");

		try
		{
			CommandLine command_line = CommandLine.parse(this.path + " " + this.args.getText());


			// set timeout
			ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
			
			
			// streams
			this.baos = new ByteArrayOutputStream();

			this.baos_err = new ByteArrayOutputStream();

			this.bais = new ByteArrayInputStream(data);
			
			
			// create executor
			DefaultExecutor executor = new DefaultExecutor();

			executor.setExitValue(this.exit_code);

			executor.setStreamHandler(new PumpStreamHandler(this.baos, this.baos_err, this.bais));

			executor.setWatchdog(watchdog);

			executor.execute(command_line, new ExecuteResultHandler(){

				public void onProcessComplete(int e) 
				{
					out.setText(baos.toString());
				}

				public void onProcessFailed(ExecuteException e) 
				{
					out.setText(baos_err.toString());
				}

			});
		}
		catch(Exception e)
		{
			out.setText(e.getMessage());
		}
	}

	public class reset_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			args.setText("");
			in.setText("");
			out.setText("");
		}
	}

	public class run_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent args)
		{
			String code = in.getText();

			execute_command(code.getBytes());
		}
	}
	
	public class about_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent args) 
		{
			out.setText("");
			
			out.append("sacmis version " + ver + "\n");
			out.append("author: Christoph \"k42b3\" Kappestein" + "\n");
			out.append("website: http://code.google.com/p/delta-quadrant" + "\n");
			out.append("license: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
			out.append("\n");
			out.append("An application wich let you write to the stdin of any executable file and" + "\n");
			out.append("return the stdout on success or stderr on error. You have to specify the" + "\n");
			out.append("executable file as first argument and optional as second argument the" + "\n");
			out.append("expected exit value (default is 0)" + "\n");
			out.append("\n");
			out.append("best regards\n");
			out.append("k42b3\n");
		}
	}
	
	public class exit_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent args) 
		{
			save_file();
			
			System.exit(0);
		}
	}
}
