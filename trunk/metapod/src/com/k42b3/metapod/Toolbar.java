/**
 * metapod
 * 
 * An application with that you can train your macro for SC2 by setting up
 * hotkeys and starting a training mode where you must build specific units
 * in a period of time.
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

package com.k42b3.metapod;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Toolbar
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Toolbar extends JPanel
{
	private JButton btn_start;
	private JButton btn_options;
	private JButton btn_about;
	private JButton btn_exit;
	
	public Toolbar()
	{
		super();
		
		this.btn_start = new JButton("Start");
		this.btn_options = new JButton("Options");
		this.btn_about = new JButton("About");
		this.btn_exit = new JButton("Exit");
		
		this.btn_start.setMnemonic(java.awt.event.KeyEvent.VK_S);
		this.btn_options.setMnemonic(java.awt.event.KeyEvent.VK_O);
		this.btn_about.setMnemonic(java.awt.event.KeyEvent.VK_A);
		this.btn_exit.setMnemonic(java.awt.event.KeyEvent.VK_Q);

		this.btn_start.setFocusable(false);
		this.btn_options.setFocusable(false);
		this.btn_about.setFocusable(false);
		this.btn_exit.setFocusable(false);
		
		this.setLayout(new FlowLayout(FlowLayout.LEADING));

		this.add(this.btn_start);
		this.add(this.btn_options);
		this.add(this.btn_about);
		this.add(this.btn_exit);
	}

	public JButton get_start()
	{
		return this.btn_start;
	}

	public JButton get_options()
	{
		return this.btn_options;
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
