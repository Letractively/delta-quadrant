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

import com.k42b3.oat.filter.ConfigFilterAbstract;

/**
 * CharsetConfig
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class CharsetConfig extends ConfigFilterAbstract
{
	private JCheckBox ckbActive;
	private JComboBox cboCharset;

	public CharsetConfig()
	{
		this.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel panel = new JPanel();		
		panel.setLayout(new GridLayout(0, 1));


		// active
		JPanel panelActive = new JPanel();
		panelActive.setLayout(new FlowLayout());

		JLabel lblActive = new JLabel("Active:");
		lblActive.setPreferredSize(new Dimension(100, 24));
		panelActive.add(lblActive);

		this.ckbActive = new JCheckBox();		
		this.ckbActive.setPreferredSize(new Dimension(200, 24));
		panelActive.add(this.ckbActive);

		panel.add(panelActive);


		// charset
		JPanel panelCharset = new JPanel();
		panelCharset.setLayout(new FlowLayout());

		JLabel lblCharset = new JLabel("Fallback charset:");
		lblCharset.setPreferredSize(new Dimension(100, 24));
		panelCharset.add(lblCharset);

		String[] charsets = {"US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16"};
		this.cboCharset = new JComboBox(charsets);
		this.cboCharset.setPreferredSize(new Dimension(200, 24));
		panelCharset.add(this.cboCharset);

		panel.add(panelCharset);


		this.add(panel);
	}

	public String getName()
	{
		return "Charset";
	}
	
	public void onLoad(Properties config) 
	{
		this.ckbActive.setSelected(true);

		this.cboCharset.setSelectedItem(config.getProperty("charset"));
	}

	public Properties onSave() 
	{
		Properties config = new Properties();
		
		Object item = this.cboCharset.getSelectedItem();
		
		if(item != null)
		{
			config.setProperty("charset", item.toString());
		}

		return config;
	}
	
	public boolean isActive()
	{
		return this.ckbActive.isSelected();
	}
}
