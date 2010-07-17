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

package com.k42b3.sacmis;

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
	private JButton btn_run;
	private JButton btn_reset;
	private JButton btn_about;
	private JButton btn_exit;
	
	public toolbar()
	{
		super();
		
		this.btn_run   = new JButton("Run");
		this.btn_reset = new JButton("Reset");
		this.btn_about = new JButton("About");
		this.btn_exit  = new JButton("Exit");
		
		this.btn_run.setMnemonic(java.awt.event.KeyEvent.VK_R);
		this.btn_reset.setMnemonic(java.awt.event.KeyEvent.VK_E);
		this.btn_about.setMnemonic(java.awt.event.KeyEvent.VK_A);
		this.btn_exit.setMnemonic(java.awt.event.KeyEvent.VK_Q);
		
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		this.add(this.btn_run);
		this.add(this.btn_reset);
		this.add(this.btn_about);
		this.add(this.btn_exit);
	}
	
	public JButton get_run()
	{
		return this.btn_run;
	}
	
	public JButton get_reset()
	{
		return this.btn_reset;
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
