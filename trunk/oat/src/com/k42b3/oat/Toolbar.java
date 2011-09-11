/**
 * oat
 * 
 * An application with that you can make raw http requests to any url. You can 
 * save a request for later use. The application uses the java nio library to 
 * make non-blocking requests so the requests should work fluently.
 * 
 * Copyright (c) 2010,2011 Christoph Kappestein <k42b3.x@gmail.com>
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
	private JButton btnSave;
	private JButton btnReset;
	private JButton btnDig;
	private JButton btnAbout;
	private JButton btnExit;
	
	public Toolbar()
	{
		super();
		
		this.btnRun   = new JButton("Run");
		this.btnSave  = new JButton("Save");
		this.btnReset = new JButton("Reset");
		this.btnDig   = new JButton("Dig");
		this.btnAbout = new JButton("About");
		this.btnExit  = new JButton("Exit");
		
		this.btnRun.setMnemonic(java.awt.event.KeyEvent.VK_R);
		this.btnSave.setMnemonic(java.awt.event.KeyEvent.VK_S);
		this.btnReset.setMnemonic(java.awt.event.KeyEvent.VK_E);
		this.btnDig.setMnemonic(java.awt.event.KeyEvent.VK_D);
		this.btnAbout.setMnemonic(java.awt.event.KeyEvent.VK_A);
		this.btnExit.setMnemonic(java.awt.event.KeyEvent.VK_Q);
		
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		this.add(this.btnRun);
		this.add(this.btnSave);
		this.add(this.btnReset);
		this.add(this.btnDig);
		this.add(this.btnAbout);
		this.add(this.btnExit);
	}
	
	public JButton getRun()
	{
		return this.btnRun;
	}
	
	public JButton getSave()
	{
		return this.btnSave;
	}
	
	public JButton getReset()
	{
		return this.btnReset;
	}

	public JButton getDig()
	{
		return this.btnDig;
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
