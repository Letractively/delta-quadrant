/**
 * $Id$
 * 
 * zubat
 * An java application to access the API of amun. It is used to debug and
 * control a website based on amun. This is the reference implementation 
 * howto access the api. So feel free to hack and extend.
 * 
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of zubat. zubat is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * zubat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with zubat. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.zubat.amun.service.my;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.k42b3.neodym.Http;
import com.k42b3.neodym.ServiceItem;

/**
 * Zubat
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Container extends JPanel implements com.k42b3.zubat.Container
{
	private static final long serialVersionUID = 1L;

	private Http http;
	private ServiceItem item;
	private ArrayList<String> fields;

	public Container()
	{
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);

		this.setBorder(new EmptyBorder(4, 4, 4, 4));
		this.setLayout(fl);
	}

	public Component getComponent() 
	{
		return this;
	}

	public void onLoad(Http http, ServiceItem item, ArrayList<String> fields) 
	{
		this.http = http;
		this.item = item;
		this.fields = fields;

		renderMy();
	}
	
	private void renderMy()
	{
		try
		{
			HashMap<String, String> account = this.requestAccount();

			URL thumbnailUrl = new URL(account.get("thumbnailUrl"));
			ImageIcon profile = new ImageIcon(thumbnailUrl);

			JLabel name = new JLabel(account.get("name"), profile, JLabel.LEFT);
			name.setFont(new Font("Serif", Font.BOLD, 32));

			this.add(name);
		}
		catch(Exception e)
		{
			JLabel err = new JLabel(e.getMessage());

			this.add(err);
		}

		this.validate();
	}

	private HashMap<String, String> requestAccount() throws Exception
	{
		Document doc = http.requestXml(Http.GET, item.getUri() + "/verifyCredentials");
		String[] fields = {"name", "profileUrl", "thumbnailUrl", "loggedIn", "gender", "group", "status", "timezone", "updated", "date"};
		HashMap<String, String> account = new HashMap<String, String>();

		// parse account
		for(int i = 0; i < fields.length; i++)
		{
			NodeList fieldList = doc.getElementsByTagName(fields[i]);

			if(fieldList.getLength() > 0)
			{
				Node fieldNode = fieldList.item(0);
				Element fieldElement = (Element) fieldNode;

				account.put(fields[i], fieldElement.getTextContent());
			}
		}

		return account;
	}
}
