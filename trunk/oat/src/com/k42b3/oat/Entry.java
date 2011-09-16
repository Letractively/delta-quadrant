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

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Entry 
{
	public static void main(String[] args)
	{
		if(args.length == 2)
		{
			System.setProperty("http.proxyHost", args[0]);
			System.setProperty("http.proxyPort", args[1]);
		}

		try
		{
			String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			//String lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();

			UIManager.setLookAndFeel(lookAndFeel);


			SwingUtilities.invokeLater(new Runnable(){
				
				public void run() 
				{
					Oat win = new Oat();
					
					win.pack();
					
					win.setVisible(true);				
				}

			});
		}
		catch(Exception e)
		{
			Oat.handleException(e);
		}
	}
}
