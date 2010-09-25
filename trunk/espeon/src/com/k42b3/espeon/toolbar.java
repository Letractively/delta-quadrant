/**
 * espeon
 * 
 * With espeon you can generate sourcecode from database structures. It was 
 * mainly developed to generate PHP classes for the psx framework (phpsx.org) 
 * but because it uses a template engine (FreeMarker) you can use it for any 
 * purpose you like.
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of espeon. espeon is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * espeon is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with espeon. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.espeon;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * toolbar
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class toolbar extends JPanel
{
	private JButton btn_connect;
	private JButton btn_generate;
	private JButton btn_about;
	private JButton btn_exit;
	
	public toolbar()
	{
		super();
		
		this.btn_connect  = new JButton("Connect");
		this.btn_generate = new JButton("Generate");
		this.btn_about    = new JButton("About");
		this.btn_exit     = new JButton("Exit");
		
		this.btn_connect.setMnemonic(java.awt.event.KeyEvent.VK_C);
		this.btn_generate.setMnemonic(java.awt.event.KeyEvent.VK_G);
		this.btn_about.setMnemonic(java.awt.event.KeyEvent.VK_A);
		this.btn_exit.setMnemonic(java.awt.event.KeyEvent.VK_Q);
		
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		this.add(this.btn_connect);
		this.add(this.btn_generate);
		this.add(this.btn_about);
		this.add(this.btn_exit);
	}
	
	public JButton get_connect()
	{
		return this.btn_connect;
	}
	
	public JButton get_generate()
	{
		return this.btn_generate;
	}
	
	public JButton get_about()
	{
		return this.btn_about;
	}
	
	public JButton get_exit()
	{
		return this.btn_exit;
	}
}
