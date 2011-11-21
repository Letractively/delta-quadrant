/**
 * Kadabra
 * 
 * Kadabra is an application to mirror a source folder to a destination folder.
 * You can create multiple projects wich are stored in an SQLite database.
 * With the option --status [id] you can see wich changes are made and
 * with --release [id] you update the changes. You can use different handler
 * like System or FTP.
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

package com.k42b3.kadabra;

import java.io.StringReader;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * FileMap
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class FileMap 
{
	private HandlerAbstract handler;
	private Document doc;

	public FileMap(HandlerAbstract handler) throws Exception
	{
		this.handler = handler;

		if(handler.isFile(getFileName()))
		{
			byte[] content = handler.getContent(getFileName());

			this.loadXml(new String(content, Charset.forName("UTF-8")));
		}
		else
		{
			throw new Exception("Map does not exist");
		}
	}

	public void loadXml(String content) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(content));

		doc = db.parse(is);
	}

	public Item[] getFiles(String path)
	{
		Element dir = this.findPath(doc.getDocumentElement(), path);
		
		if(dir != null)
		{
			NodeList childs = dir.getChildNodes();
			int len = 0;

			for(int i = 0; i < childs.getLength(); i++)
			{
				if(childs.item(i) instanceof Element)
				{
					len++;
				}
			}

			Item[] items = new Item[len];
			int c = 0;

			for(int i = 0; i < childs.getLength(); i++)
			{
				if(childs.item(i) instanceof Element)
				{
					Element el = (Element) childs.item(i);

					if(el.getNodeName().equals("dir"))
					{
						items[c] = new Item(el.getAttribute("name"), Item.DIRECTORY);
					}

					if(el.getNodeName().equals("file"))
					{
						items[c] = new Item(el.getAttribute("name"), Item.FILE, el.getAttribute("md5"));
					}

					c++;
				}
			}

			return items;
		}
		else
		{
			return null;
		}
	}

	private Element findPath(Element parent, String path)
	{
		String name;
		path = path.trim();

		if(!path.isEmpty() && path.charAt(0) == '/')
		{
			path = path.substring(1);
		}

		if(path.indexOf('/') != -1)
		{
			name = path.substring(0, path.indexOf('/'));
			path = path.substring(path.indexOf('/') + 1);
		}
		else
		{
			name = path;
			path = null;
		}

		if(name.isEmpty() || name.equals("."))
		{
			return parent;
		}

		NodeList list = parent.getChildNodes();

		for(int i = 0; i < list.getLength(); i++)
		{
			if(list.item(i) instanceof Element)
			{
				Element el = (Element) list.item(i);
				
				if(el.getNodeName().equals("dir") && el.getAttribute("name").equals(name))
				{
					if(path != null)
					{
						return findPath(el, path);
					}
					else
					{
						return el;
					}
					
				}
			}
		}

		return null;
	}

	public static void generate(HandlerAbstract handler) throws Exception
	{
		StringBuilder resp = new StringBuilder();

		resp = new StringBuilder();
		resp.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		resp.append("<kadabra xmlns=\"http://ns.k42b3.com/2011/kadabra\">\n");
		build(handler, resp, ".");
		resp.append("</kadabra>");

		handler.uploadFile(FileMap.getFileName(), resp.toString().getBytes(Charset.forName("UTF-8")));
	}

	private static void build(HandlerAbstract handler, StringBuilder resp, String path) throws Exception
	{
		Item[] files = handler.getFiles(path);

		for(int i = 0; i < files.length; i++)
		{
			Item item = files[i];

			// check whether not current or up dir
			if(item.getName().equals(".") || item.getName().equals("..") || item.getName().equals(FileMap.getFileName()))
			{
				continue;
			}

			if(item.isDirectory())
			{
				resp.append("<dir name=\"" + item.getName() + "\">\n");

				build(handler, resp, path + "/" + item.getName());

				resp.append("</dir>\n");
			}

			if(item.isFile())
			{
				byte[] content = handler.getContent(path + "/" + item.getName());
				String md5 = DigestUtils.md5Hex(Kadabra.normalizeContent(content));

				resp.append("<file name=\"" + item.getName() + "\" md5=\"" + md5 + "\" />\n");
			}
		}
	}
	
	public static String getFileName()
	{
		return ".kadabra.xml";
	}
}
