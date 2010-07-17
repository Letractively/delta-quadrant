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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

/**
 * filter_in
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class filter_in extends JFrame
{
	public static boolean active = false;

	private ArrayList<config_filter> filters_config = new ArrayList<config_filter>();
	private ArrayList<irequest_filter> filters = new ArrayList<irequest_filter>();
	
	private icallback callback;
	
	public filter_in(icallback callback)
	{
		filter_in.active = true;
		
		
		this.callback = callback;
		
		
		this.setTitle("Request filter");

		this.setLocation(100, 100);

		this.setSize(400, 300);

		this.setMinimumSize(this.getSize());

		this.setLayout(new BorderLayout());


		JTabbedPane panel = new JTabbedPane();

		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));


		// add filters
		ArrayList<String> filters = new ArrayList<String>();
		
		filters.add("basicauth");
		filters.add("oauth");

		
		// parse filters
		for(int i = 0; i < filters.size(); i++)
		{
			try
			{
				String cls_config = "com.k42b3.oat.http.filter_request." + filters.get(i) + "_config";
				String cls = "com.k42b3.oat.http.filter_request." + filters.get(i);

				Class c_config = Class.forName(cls_config);
				Class c = Class.forName(cls);

				config_filter filter_config = (config_filter) c_config.newInstance();
				irequest_filter filter = (irequest_filter) c.newInstance();
				
				this.filters_config.add(filter_config);
				this.filters.add(filter);
				
				
				JScrollPane scp_filter = new JScrollPane(filter_config);

				scp_filter.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
				
				scp_filter.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

				scp_filter.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

				
				panel.addTab(filter_config.get_name(), scp_filter);
			}
			catch(Exception e)
			{
			}
		}
	

		this.add(panel, BorderLayout.CENTER);
		
		
		// buttons
		JPanel panel_buttons = new JPanel();
		
		panel_buttons.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		
		JButton btn_save = new JButton("Save");
		
		btn_save.addActionListener(new handler_save());
		
		panel_buttons.add(btn_save);
		
		
		JButton btn_cancel = new JButton("Cancel");
		
		btn_cancel.addActionListener(new handler_cancel());
		
		panel_buttons.add(btn_cancel);
		

		this.add(panel_buttons, BorderLayout.SOUTH);
	}
	
	private void close()
	{
		this.setVisible(false);
		
		filter_in.active = false;
	}
	
	class handler_save implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			ArrayList<irequest_filter> list = new ArrayList<irequest_filter>();
			
			for(int i = 0; i < filters_config.size(); i++)
			{
				if(filters_config.get(i).is_active())
				{
					filters.get(i).set_config(filters_config.get(i).on_save());
					
					list.add(filters.get(i));
				}
			}


			callback.response(list);
			
			
			close();
		}
	}
	
	class handler_cancel implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			close();
		}
	}
}
