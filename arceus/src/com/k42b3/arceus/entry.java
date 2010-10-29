/**
 * arceus
 * 
 * arceus is a little program to train to convert numbers into different
 * number systems with different bases.
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

package com.k42b3.arceus;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * arceus
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class entry 
{
	public static void main(String[] args)
	{
		try
		{
			String look_and_feel = UIManager.getSystemLookAndFeelClassName();

			UIManager.setLookAndFeel(look_and_feel);


			SwingUtilities.invokeLater(new Runnable(){
				
				public void run() 
				{
					arceus win = new arceus();
					
					win.pack();
					
					win.setVisible(true);				
				}
				
			});
		}
		catch(Exception e)
		{
			System.out.print(e.getMessage());
		}	
	}
}
