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

package com.k42b3.oat.http.filterResponse;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.k42b3.oat.ConfigFilter;

/**
 * gzip_config
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class GzipConfig extends ConfigFilter
{
	private JCheckBox ckb_active;
	
	public String getName()
	{
		return "GZip";
	}
	
	public Properties onSave() 
	{
		return null;
	}
	
	public boolean isActive()
	{
		return this.ckb_active.isSelected();
	}
	
	public GzipConfig()
	{
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JPanel panel = new JPanel();
		
		panel.setLayout(new GridLayout(0, 1));
		
		
		// active
		JPanel panel_active = new JPanel();

		panel_active.setLayout(new FlowLayout());


		JLabel lbl_active = new JLabel("Active:");

		lbl_active.setPreferredSize(new Dimension(100, 24));

		panel_active.add(lbl_active);


		this.ckb_active = new JCheckBox();
		
		this.ckb_active.setPreferredSize(new Dimension(200, 24));
		
		panel_active.add(this.ckb_active);
		
		
		panel.add(panel_active);
		
		
		this.add(panel);
	}
}
