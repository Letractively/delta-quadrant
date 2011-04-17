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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Logger;

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
public class FilterIn extends JFrame
{
	public static boolean active = false;

	private ArrayList<ConfigFilter> filtersConfig = new ArrayList<ConfigFilter>();
	private ArrayList<RequestFilterInterface> filters = new ArrayList<RequestFilterInterface>();
	
	private CallbackInterface callback;
	
	private Logger logger;

	public FilterIn(CallbackInterface callback)
	{
		logger = Logger.getLogger("com.k42b3.oat");


		FilterIn.active = true;
		
		
		this.callback = callback;
		
		
		this.setTitle("Request filter");

		this.setLocation(100, 100);

		this.setSize(400, 300);

		this.setMinimumSize(this.getSize());
		
		this.setResizable(false);

		this.setLayout(new BorderLayout());


		JTabbedPane panel = new JTabbedPane();

		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));


		// add filters
		ArrayList<String> filters = new ArrayList<String>();
		
		filters.add("BasicAuth");
		filters.add("ContentLength");
		filters.add("Oauth");
		filters.add("UserAgent");

		
		// parse filters
		for(int i = 0; i < filters.size(); i++)
		{
			try
			{
				String clsConfig = "com.k42b3.oat.http.filterRequest." + filters.get(i) + "Config";
				String cls = "com.k42b3.oat.http.filterRequest." + filters.get(i);

				Class c_config = Class.forName(clsConfig);
				Class c = Class.forName(cls);

				ConfigFilter filterConfig = (ConfigFilter) c_config.newInstance();
				RequestFilterInterface filter = (RequestFilterInterface) c.newInstance();
				
				this.filtersConfig.add(filterConfig);
				this.filters.add(filter);
				
				
				JScrollPane scpFilter = new JScrollPane(filterConfig);

				scpFilter.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
				
				scpFilter.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

				scpFilter.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

				
				panel.addTab(filterConfig.getName(), scpFilter);
			}
			catch(Exception e)
			{
				logger.warning(e.getMessage());
			}
		}
	

		this.add(panel, BorderLayout.CENTER);
		
		
		// buttons
		JPanel panelButtons = new JPanel();
		
		panelButtons.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		
		JButton btnSave = new JButton("Save");
		
		btnSave.addActionListener(new handler_save());
		
		panelButtons.add(btnSave);
		
		
		JButton btnCancel = new JButton("Cancel");
		
		btnCancel.addActionListener(new handler_cancel());
		
		panelButtons.add(btnCancel);
		

		this.add(panelButtons, BorderLayout.SOUTH);
	}
	
	private void close()
	{
		this.setVisible(false);
		
		FilterIn.active = false;
	}
	
	class handler_save implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			ArrayList<RequestFilterInterface> list = new ArrayList<RequestFilterInterface>();
			
			for(int i = 0; i < filtersConfig.size(); i++)
			{
				if(filtersConfig.get(i).isActive())
				{
					filters.get(i).set_config(filtersConfig.get(i).onSave());
					
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
