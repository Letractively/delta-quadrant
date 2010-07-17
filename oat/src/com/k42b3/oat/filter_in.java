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
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
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

	public filter_in()
	{
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
				String cls = "com.k42b3.oat.http.filter_request." + filters.get(i) + "_config";

				Class c = Class.forName(cls);

				config_filter filter = (config_filter) c.newInstance();
				
				
				JScrollPane scp_filter = new JScrollPane(filter);

				scp_filter.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
				
				scp_filter.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

				scp_filter.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	

				
				panel.addTab(filter.get_name(), scp_filter);
			}
			catch(Exception e)
			{
			}
		}
	

		this.add(panel, BorderLayout.CENTER);
	}
}
