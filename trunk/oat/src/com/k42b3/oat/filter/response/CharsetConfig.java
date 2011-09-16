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

package com.k42b3.oat.filter.response;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.k42b3.oat.filter.ConfigFilter;

/**
 * CharsetConfig
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class CharsetConfig extends ConfigFilter
{
	private JCheckBox ckb_active;
	private JComboBox cbo_charset;
	
	public String getName()
	{
		return "Charset";
	}
	
	public Properties onSave() 
	{
		Properties props = new Properties();
		
		Object item = this.cbo_charset.getSelectedItem();
		
		if(item != null)
		{
			props.setProperty("charset", item.toString());
		}

		return props;
	}
	
	public boolean isActive()
	{
		return this.ckb_active.isSelected();
	}
	
	public CharsetConfig()
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
		
		
		
		// charset
		JPanel panel_charset = new JPanel();

		panel_charset.setLayout(new FlowLayout());


		JLabel lbl_charset = new JLabel("Fallback charset:");

		lbl_charset.setPreferredSize(new Dimension(100, 24));

		panel_charset.add(lbl_charset);


		String[] charsets = {"US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16"};

		this.cbo_charset = new JComboBox(charsets);
		
		this.cbo_charset.setPreferredSize(new Dimension(200, 24));

		panel_charset.add(this.cbo_charset);


		panel.add(panel_charset);
		
		
		this.add(panel);
	}
}
