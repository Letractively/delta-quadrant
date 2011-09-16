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

import java.awt.Color;
import java.awt.Font;
import java.util.Map;

import javax.swing.JTextArea;

import com.k42b3.oat.http.Util;

/**
 * Out
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Out extends JTextArea
{
	public Out()
	{
		super();

		this.setFont(new Font("Courier New", Font.PLAIN, 12));

		this.setEditable(false);

		this.setBackground(new Color(255, 255, 255));

		this.setForeground(new Color(0, 0, 0));
	}

	public String getHeader(String key)
	{
		Map<String, String> header = this.getHeader();

		return header.get(key);
	}

	public Map<String, String> getHeader()
	{
		return Util.parseHeader(this.getRawHeader(), "\n");
	}

	public String getBody()
	{
		int pos = this.getText().indexOf("\n\n");

		if(pos != -1)
		{
			return this.getText().substring(pos + 2);
		}
		else
		{
			return "";
		}
	}

	public void setBody(String content)
	{
		int pos = this.getText().indexOf("\n\n");

		if(pos != -1)
		{
			this.setText(this.getRawHeader() + "\n\n" + content);
		}
	}

	private String getRawHeader()
	{
		int pos = this.getText().indexOf("\n\n");
		String rawHeader;

		if(pos != -1)
		{
			rawHeader = this.getText().substring(0, pos);
		}
		else
		{
			rawHeader = this.getText();
		}

		return rawHeader;
	}
}
