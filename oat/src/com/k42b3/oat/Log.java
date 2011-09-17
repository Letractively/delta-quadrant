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

package com.k42b3.oat;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Log
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Log extends JFrame
{
	private JTextArea txtLog;

	private Logger logger = Logger.getLogger("com.k42b3.oat");

	public Log()
	{
		// settings
		this.setTitle("Log");
		this.setLocation(100, 100);
		this.setSize(360, 400);
		this.setMinimumSize(this.getSize());
		this.setResizable(false);
		this.setLayout(new BorderLayout());


		// tab panel
		txtLog = new Out();
		txtLog.setEditable(false);

		this.add(new JScrollPane(txtLog), BorderLayout.CENTER);


		// buttons
		JPanel panelButtons = new JPanel();

		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);

		panelButtons.setLayout(fl);

		JButton btnReset = new JButton("Reset");
		btnReset.setMnemonic(java.awt.event.KeyEvent.VK_R);
		btnReset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				txtLog.setText("");
			}

		});

		panelButtons.add(btnReset);

		this.add(panelButtons, BorderLayout.SOUTH);
	}
	
	public void append(LogRecord rec)
	{
		txtLog.append(rec.getLevel() + ": " + rec.getMessage() + "\n");
	}
}
