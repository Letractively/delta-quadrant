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
public class Sacmis extends JFrame
{
	public static String ver = "0.0.5 beta";
	public static String file = "input.cache";

	private String path;
	private int exitCode;
	private long timeout = 4000;
	
	private JTextField args;
	private In in;
	private Out out;
	private Toolbar toolbar;

	private ByteArrayOutputStream baos;
	private ByteArrayOutputStream baosErr;
	private ByteArrayInputStream bais;
	
	public Sacmis(String path, int exitCode) throws Exception
	{
		this.path = path;
		this.exitCode = exitCode;

		
		this.setTitle("sacmis (version: " + ver + ")");
		
		this.setLocation(100, 100);
		
		this.setSize(600, 500);
		
		this.setMinimumSize(this.getSize());

		
		// arguments
		JPanel panelArgs = new JPanel();
		
		panelArgs.setLayout(new BorderLayout());
		
		panelArgs.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		
		this.args = new Args();

		panelArgs.add(this.args, BorderLayout.CENTER);
		
		
		this.add(panelArgs, BorderLayout.NORTH);
		
		
		// main panel
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		
		this.in  = new In();
		
		JScrollPane scrIn = new JScrollPane(this.in);
		
		scrIn.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		scrIn.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		scrIn.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		sp.add(scrIn);
		
		
		this.out = new Out();
		
		JScrollPane scrOut = new JScrollPane(this.out);
		
		scrOut.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		scrOut.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		scrOut.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

		sp.add(scrOut);
		
				
		this.add(sp, BorderLayout.CENTER);

		
		// toolbar
		this.toolbar = new Toolbar();
		
		this.toolbar.getRun().addActionListener(new runHandler());
		this.toolbar.getReset().addActionListener(new resetHandler());
		this.toolbar.getAbout().addActionListener(new aboutHandler());
		this.toolbar.getExit().addActionListener(new exitHandler());
		
		this.getContentPane().add(this.toolbar, BorderLayout.SOUTH);
		
		
		this.setVisible(true);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		this.loadFile();
	}
	
	private void loadFile()
	{
		File fIn = new File(file);
		FileInputStream fileIn;
	
		in.setText("");
		out.setText("");
	
		if(fIn.exists())
		{
			try
			{
				fileIn = new FileInputStream(file);

				BufferedReader br_in = new BufferedReader(new InputStreamReader(fileIn));

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

	private void saveFile()
	{
		FileOutputStream fileOut;
		
		try
		{
			fileOut = new FileOutputStream(file);

		    new PrintStream(fileOut).print(in.getText());

		    fileOut.close();		
		}
		catch(IOException e)
		{
			out.setText(e.getMessage());
		}
	}
	
	private void executeCommand(byte[] data)
	{
		out.setText("");

		try
		{
			CommandLine commandLine = CommandLine.parse(this.path + " " + this.args.getText());


			// set timeout
			ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
			
			
			// streams
			this.baos = new ByteArrayOutputStream();

			this.baosErr = new ByteArrayOutputStream();

			this.bais = new ByteArrayInputStream(data);
			
			
			// create executor
			DefaultExecutor executor = new DefaultExecutor();

			executor.setExitValue(this.exitCode);

			executor.setStreamHandler(new PumpStreamHandler(this.baos, this.baosErr, this.bais));

			executor.setWatchdog(watchdog);

			executor.execute(commandLine, new ExecuteResultHandler(){

				public void onProcessComplete(int e) 
				{
					out.setText(baos.toString());
				}

				public void onProcessFailed(ExecuteException e) 
				{
					out.setText(baosErr.toString());
				}

			});
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
			args.setText("");
			in.setText("");
			out.setText("");
		}
	}

	public class runHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent args)
		{
			String code = in.getText();

			executeCommand(code.getBytes());
		}
	}
	
	public class aboutHandler implements ActionListener
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
	
	public class exitHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent args) 
		{
			saveFile();
			
			System.exit(0);
		}
	}
}
