/**
 * oat
 * 
 * An application with that you can make raw http requests to any url. You can 
 * save a request for later use. The application uses the java nio library to 
 * make non-blocking requests so the requests should work fluently.
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;

/**
 * Form
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 165 $
 */
public class Form extends JFrame
{
	private CallbackInterface cb;
	private HashMap<String, String> values = new HashMap<String, String>();

	public Form()
	{
		// settings
		this.setTitle("oat " + Oat.VERSION);
		this.setLocation(100, 100);
		this.setSize(500, 400);
		this.setMinimumSize(this.getSize());
		this.setLayout(new BorderLayout());
	}
	
	public void parseHtml(String html)
	{
		boolean inForm = false;
		boolean inInputTag = false;
		boolean inTextareaTag = false;
		boolean inSelectTag = false;

		for(int i = 0; i < html.length(); i++)
		{
			if(inInputTag)
			{
				if(this.startsWith("<input", i, html))
				{
					inInputTag = true;
				}
			}

			if(!inForm)
			{
				if(this.startsWith("<form", i, html))
				{
					inForm = true;
				}
			}
			else
			{
				if(this.startsWith("<input", i, html))
				{
					inInputTag = true;
				}

				if(this.startsWith("<textarea", i, html))
				{
					inTextareaTag = true;
				}

				if(this.startsWith("<select", i, html))
				{
					inSelectTag = true;
				}

				if(this.startsWith("</form>", i, html))
				{
					inForm = false;
				}
			}
		}
	}

	public void setCallback(CallbackInterface cb)
	{
		this.cb = cb;
	}
	
	private boolean startsWith(String phrase, int index, String content)
	{
		for(int i = 0; i < phrase.length(); i++)
		{
			if(Character.toLowerCase(content.charAt(index + i)) != Character.toLowerCase(phrase.charAt(i)))
			{
				return false;
			}
		}

		return true;	
	}

	public class setHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
		}
	}
}
