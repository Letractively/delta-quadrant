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

import javax.swing.UIManager;

/**
 * entry
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
        try
        {
    		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

        	UIManager.setLookAndFeel(lookAndFeel);


        	// bin path
        	String path = "";

        	if(args.length > 0)
        	{
        		path = args[0];
        	}

        	if(path.isEmpty())
        	{
        		throw new Exception("You must provide a path to an executable file");
        	}


        	// tmp file
        	String file = "input.cache";

        	if(args.length > 1)
        	{
        		file = args[1];
        	}


        	// exit code
        	int exitCode = 0;

        	if(args.length > 2)
        	{
        		try
        		{
        			exitCode = Integer.parseInt(args[2]);
        		}
        		catch(NumberFormatException e)
        		{
        		}
        	}


        	// write to stdin
        	boolean writerStdIn = false;

        	if(args.length > 3)
        	{
        		writerStdIn = Boolean.parseBoolean(args[3]);
        	}


        	// start sacmis
        	new Sacmis(path, file, exitCode, writerStdIn);
        }
        catch(Exception e)
        {
        	System.err.print(e.getMessage());
        }
	}
}
