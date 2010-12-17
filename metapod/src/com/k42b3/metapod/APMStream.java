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

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JPanel;

/**
 * APMStream
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class APMStream extends JPanel
{
	private double apm;
	private int time;
	private int solving_time;
	private int solved;
	private int failed;

	public void repaint(int apm, int time, int solving_time, int solved, int failed)
	{
		this.apm = (apm / time) * 60;

		this.time = time;
		this.solving_time = solving_time;

		this.solved = solved;
		this.failed = failed;

		this.repaint();
	}

	protected void paintComponent(Graphics g)
	{
		g.setColor(this.getBackground());
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.BLACK);


		// apm
		g.drawString("APM: " + this.apm, 10, 20);


		// time
		g.drawString("Time: " + this.convert_seconds(this.time) + " complete /  " + this.convert_seconds(this.solving_time / 1000) + " per round", 100, 20);


		// points
		g.drawString(this.solved + " Solved / " + this.failed + " Failed", 380, 20);
	}
	
	private String convert_seconds(int seconds)
	{
		if(seconds < 60)
		{
			return seconds + " seconds";
		}
		else if(seconds < 60 * 60)
		{
			return seconds / 60 + " minutes";
		}
		else
		{
			return seconds / 60 * 60 + " hours";
		}
	}
}
