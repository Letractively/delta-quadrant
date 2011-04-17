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
 * This file is part of sacmis. sacmis is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * sacmis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with sacmis. If not, see <http://www.gnu.org/licenses/>.
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
public class Toolbar extends JPanel
{
	private JButton btnRun;
	private JButton btnReset;
	private JButton btnAbout;
	private JButton btnExit;
	
	public Toolbar()
	{
		super();
		
		this.btnRun   = new JButton("Run");
		this.btnReset = new JButton("Reset");
		this.btnAbout = new JButton("About");
		this.btnExit  = new JButton("Exit");
		
		this.btnRun.setMnemonic(java.awt.event.KeyEvent.VK_R);
		this.btnReset.setMnemonic(java.awt.event.KeyEvent.VK_E);
		this.btnAbout.setMnemonic(java.awt.event.KeyEvent.VK_A);
		this.btnExit.setMnemonic(java.awt.event.KeyEvent.VK_Q);
		
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		this.add(this.btnRun);
		this.add(this.btnReset);
		this.add(this.btnAbout);
		this.add(this.btnExit);
	}
	
	public JButton getRun()
	{
		return this.btnRun;
	}
	
	public JButton getReset()
	{
		return this.btnReset;
	}
	
	public JButton getAbout()
	{
		return this.btnAbout;
	}
	
	public JButton getExit()
	{
		return this.btnExit;
	}
}
