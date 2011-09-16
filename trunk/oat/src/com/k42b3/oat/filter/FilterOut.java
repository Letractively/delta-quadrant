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

package com.k42b3.oat.filter;

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
 * FilterOut
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class FilterOut extends JFrame
{
	public static boolean active = false;

	private ArrayList<ConfigFilter> filtersConfig = new ArrayList<ConfigFilter>();
	private ArrayList<ResponseFilterInterface> filters = new ArrayList<ResponseFilterInterface>();

	private CallbackInterface callback;

	private Logger logger;
	
	public FilterOut(CallbackInterface callback)
	{
		logger = Logger.getLogger("com.k42b3.oat");


		FilterOut.active = true;
		
		
		this.callback = callback;
		
		
		this.setTitle("Response filter");

		this.setLocation(100, 100);

		this.setSize(400, 300);
		
		this.setMinimumSize(this.getSize());
		
		this.setResizable(false);
		
		this.setLayout(new BorderLayout());


		JTabbedPane panel = new JTabbedPane();

		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));


		// add filters
		ArrayList<String> filters = new ArrayList<String>();

		filters.add("Charset");
		filters.add("Gzip");

		
		// parse filters
		for(int i = 0; i < filters.size(); i++)
		{
			try
			{
				String clsConfig = "com.k42b3.oat.http.filterResponse." + filters.get(i) + "Config";
				String cls = "com.k42b3.oat.http.filterResponse." + filters.get(i);

				Class c_config = Class.forName(clsConfig);
				Class c = Class.forName(cls);

				ConfigFilter filterConfig = (ConfigFilter) c_config.newInstance();
				ResponseFilterInterface filter = (ResponseFilterInterface) c.newInstance();
				
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
		
		btnSave.addActionListener(new HandlerSave());
		
		panelButtons.add(btnSave);
		
		
		JButton btnCancel = new JButton("Cancel");
		
		btnCancel.addActionListener(new HandlerCancel());
		
		panelButtons.add(btnCancel);
		

		this.add(panelButtons, BorderLayout.SOUTH);
	}
	
	private void close()
	{
		this.setVisible(false);
		
		FilterOut.active = false;
	}
	
	class HandlerSave implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			ArrayList<ResponseFilterInterface> list = new ArrayList<ResponseFilterInterface>();

			for(int i = 0; i < filtersConfig.size(); i++)
			{
				if(filtersConfig.get(i).isActive())
				{
					filters.get(i).setConfig(filtersConfig.get(i).onSave());
					
					list.add(filters.get(i));
				}
			}


			callback.response(list);
			

			close();
		}
	}
	
	class HandlerCancel implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			close();
		}
	}
}
