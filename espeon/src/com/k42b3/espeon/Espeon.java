/**
 * espeon
 * 
 * With espeon you can generate sourcecode from database structures. It was 
 * mainly developed to generate PHP classes for the psx framework (phpsx.org) 
 * but because it uses a template engine (FreeMarker) you can use it for any 
 * purpose you like.
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

package com.k42b3.espeon;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.k42b3.espeon.gui.Main;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * espeon
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Espeon
{
	public static String version = "0.0.4 beta";
	public static String path = "templates";

	private Connection con;
	private Configuration cfg;

	public Espeon() throws Exception
	{
		// set template config
		File templatePath = new File(Espeon.path);
		
		if(templatePath.isDirectory())
		{
			this.cfg = new Configuration();

			this.cfg.setDirectoryForTemplateLoading(new File(Espeon.path));

			this.cfg.setObjectWrapper(new DefaultObjectWrapper());
		}
		else
		{
			throw new Exception("You have to create a dir called '" + Espeon.path + "' where the templates are located");
		}
	}

	public void connect(String host, String db, String user, String pw) throws Exception
	{
		con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + db + "?user=" + user + "&amp;password=" + pw);
	}

	public void generate(ArrayList<String> templates, HashMap<String, HashMap<String, Object>> tables)
	{
		for(int j = 0; j < templates.size(); j++)
		{
			try
			{
				Iterator<Entry<String, HashMap<String, Object>>> it = tables.entrySet().iterator();

				while(it.hasNext())
				{
					Entry<String, HashMap<String, Object>> entry = it.next();

					Template temp = cfg.getTemplate(templates.get(j));

					Writer out = new FileWriter(entry.getKey() + "-" + templates.get(j));

					temp.process(entry.getValue(), out);

					out.flush();
				}
			}
			catch(Exception e)
			{
				Espeon.handleException(e);
			}
		}
	}

	public Connection getConnection()
	{
		return con;
	}

	public Configuration getConfiguration()
	{
		return cfg;
	}

	public HashMap<String, Object> getParams(String table) throws Exception
	{
		HashMap<String, Object> params = new HashMap<String, Object>();
		Object[][] rows = this.getTableStructure(table);

		Object firstColumn = "";
		Object lastColumn = "";
		Object primaryKey = "";
		ArrayList<Object> unqiueKey = new ArrayList<Object>();
		ArrayList<Object> fields = new ArrayList<Object>();
		ArrayList<HashMap<String, String>> columns = new ArrayList<HashMap<String, String>>();

		for(int i = 0; i < rows.length; i++)
		{
			String rField   = rows[i][0] != null ? rows[i][0].toString() : "";
			String rType    = rows[i][1] != null ? rows[i][1].toString() : "";
			String rNull    = rows[i][2] != null ? rows[i][2].toString() : "";
			String rKey     = rows[i][3] != null ? rows[i][3].toString() : "";
			String rDefault = rows[i][4] != null ? rows[i][4].toString() : "";
			String rExtra   = rows[i][5] != null ? rows[i][5].toString() : "";

			lastColumn = rField;

			if(i == 0)
			{
				firstColumn = rField;
			}

			if(rKey.equals("PRI"))
			{
				primaryKey = rField;
			}

			if(rKey.equals("UNI"))
			{
				unqiueKey.add(rField);
			}


			fields.add(rField);


			String rLength = "";
			int pos = rType.indexOf('(');

			if(pos != -1)
			{
				String rawLength = rType.substring(pos + 1);
				rLength = rawLength.substring(0, rawLength.length() - 1);
				rType = rType.substring(0, pos);
			}

			HashMap<String, String> c = new HashMap<String, String>();

			c.put("field", rField);
			c.put("type", rType);
			c.put("length", rLength);
			c.put("null", rNull);
			c.put("key", rKey);
			c.put("default", rDefault);
			c.put("extra", rExtra);

			columns.add(c);
		}

		int pos = table.lastIndexOf('_');

		params.put("table", table);
		params.put("name", Espeon.convertTableToClass(table.substring(pos + 1)));
		params.put("ns", Espeon.convertTableToClass(table.substring(0, pos)));
		params.put("firstColumn", firstColumn);
		params.put("lastColumn", lastColumn);
		params.put("primaryKey", primaryKey);
		params.put("unqiueKey", unqiueKey);
		params.put("fields", fields);
		params.put("columns", columns);

		return params;
	}

	public Object[][] getTableStructure(String table) throws Exception
	{
		PreparedStatement ps = con.prepareStatement("DESCRIBE " + table);

		ps.execute();

		ResultSet result = ps.getResultSet();

		result.last();

		Object[][] rows = new Object[result.getRow()][6];

		result.beforeFirst();

		while(result.next())
		{
			int row = result.getRow() - 1;

			rows[row][0] = result.getString("Field");
			rows[row][1] = result.getString("Type");
			rows[row][2] = result.getString("Null");
			rows[row][3] = result.getString("Key");
			rows[row][4] = result.getString("Default");
			rows[row][5] = result.getString("Extra");
		}

		return rows;
	}

	public void runGui() throws Exception
	{
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

		UIManager.setLookAndFeel(lookAndFeel);


		com.k42b3.espeon.gui.Main panel = new com.k42b3.espeon.gui.Main(this);

		registerViewCallbacks(panel);


		panel.pack();

		panel.setVisible(true);
	}

	public void runCmd(String[] args)
	{
		com.k42b3.espeon.cmd.Main panel = new com.k42b3.espeon.cmd.Main(this);

		registerViewCallbacks(panel);
	}

	private void registerViewCallbacks(View view)
	{
		view.setConnectCallback(new ConnectCallback() {

			public void onConnect(String host, String db, String user, String pw) throws Exception
			{
				connect(host, db, user, pw);
			}

		});

		view.setGenerateCallback(new GenerateCallback() {

			public void onGenerate(ArrayList<String> templates, HashMap<String, HashMap<String, Object>> tables) throws Exception 
			{
				generate(templates, tables);
			}

		});
	}

	public static String getAbout()
	{
		StringBuilder out = new StringBuilder();

		out.append("Version: espeon " + version + "\n");
		out.append("Author: Christoph \"k42b3\" Kappestein" + "\n");
		out.append("Website: http://code.google.com/p/delta-quadrant" + "\n");
		out.append("License: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
		out.append("\n");
		out.append("With espeon you can generate sourcecode from database structures. It was" + "\n");
		out.append("mainly developed to generate PHP classes for the PSX framework (phpsx.org)" + "\n");
		out.append("but because it uses a template engine (FreeMarker) you can use it for any" + "\n");
		out.append("purpose you like." + "\n");

		return out.toString();
	}

	public static String convertTableToClass(String table)
	{
		String[] parts = table.split("_");
		String className = "";
		int length = parts.length;

		for(int i = 0; i < parts.length; i++)
		{
			className+= Character.toUpperCase(parts[i].charAt(0)) + parts[i].substring(1);

			if(i < length - 1)
			{
				className+= "_";
			}
		}

		return className;
	}

	public static String convertTableToPath(String table)
	{
		return Espeon.convertTableToClass(table).replace('_', '/');
	}

	public static void handleException(Exception e)
	{
		e.printStackTrace();
	}
}
