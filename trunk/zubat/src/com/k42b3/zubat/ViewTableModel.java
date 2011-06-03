/**
 * zubat
 * 
 * An java application to access the API of amun. It is used to debug and
 * control a website based on amun. This is the reference implementation 
 * howto access the api. So feel free to hack and extend.
 * 
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
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

package com.k42b3.zubat;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ViewTablelModel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class ViewTableModel extends AbstractTableModel
{
	protected String baseUrl;
	protected String url;
	protected Http http;
	protected Logger logger;

	protected ArrayList<String> supportedFields = new ArrayList<String>();
	protected ArrayList<String> fields = new ArrayList<String>();
	protected Object[][] rows;

	protected int totalResults;
	protected int startIndex;
	protected int itemsPerPage;

	public ViewTableModel(String url, Http http) throws Exception
	{
		this.baseUrl = url;
		this.url = url;
		this.http = http;
		this.logger = Logger.getLogger("com.k42b3.zubat");

		this.requestSupportedFields(url);
	}

	public void loadData(ArrayList<String> fields) throws Exception
	{
		this.fields = fields;

		this.request(url);
	}

	public void loadData() throws Exception
	{
		this.fields = null;

		this.request(url);
	}

	public void nextPage() throws Exception
	{
		int index = startIndex + itemsPerPage;

		String url = Http.appendQuery(this.url, "count=" + itemsPerPage + "&startIndex=" + index);

		this.request(url);
	}

	public void prevPage() throws Exception
	{
		int index = startIndex - itemsPerPage;
		index = index < 0 ? 0 : index;

		String url = Http.appendQuery(this.url, "count=" + itemsPerPage + "&startIndex=" + index);

		this.request(url);
	}

	public String getBaseUrl()
	{
		return baseUrl;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public ArrayList<String> getSupportedFields()
	{
		return this.supportedFields;
	}

	public ArrayList<String> getFields()
	{
		return fields;
	}

	public int getTotalResults()
	{
		return totalResults;
	}

	public int getStartIndex()
	{
		return startIndex;
	}

	public int getItemsPerPage()
	{
		return itemsPerPage;
	}

	public int getColumnCount()
	{
		return fields.size();
	}

	public String getColumnName(int columnIndex)
	{
		return fields.get(columnIndex);
	}

	public int getRowCount() 
	{
		return rows.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(rowIndex >= 0 && rowIndex < rows.length)
		{
			if(columnIndex >= 0 && columnIndex < rows[rowIndex].length)
			{
				return rows[rowIndex][columnIndex];
			}
		}

		return null;
	}
	
	private void request(String url) throws Exception
	{
		// request
		if(fields != null)
		{
			StringBuilder queryFields = new StringBuilder();

			for(int i = 0; i < fields.size(); i++)
			{
				if(this.supportedFields.contains(fields.get(i)))
				{
					queryFields.append(fields.get(i) + ",");
				}
			}

			url = Http.appendQuery(url, "fields=" + queryFields.substring(0, queryFields.length() - 1));
		}


		Document doc = http.requestXml(Http.GET, url);


		// get meta
		Element totalResultsElement = (Element) doc.getElementsByTagName("totalResults").item(0);
		Element startIndexElement = (Element) doc.getElementsByTagName("startIndex").item(0);
		Element itemsPerPageElement = (Element) doc.getElementsByTagName("itemsPerPage").item(0);
		NodeList entry = doc.getElementsByTagName("entry");

		if(totalResultsElement != null)
		{
			totalResults = Integer.parseInt(totalResultsElement.getTextContent());
		}

		if(startIndexElement != null)
		{
			startIndex = Integer.parseInt(startIndexElement.getTextContent());
		}

		if(itemsPerPageElement != null)
		{
			itemsPerPage = Integer.parseInt(itemsPerPageElement.getTextContent());
		}


		// build row
		rows = new Object[entry.getLength()][fields.size()];


		// parse entries
		NodeList entryList = doc.getElementsByTagName("entry");

		for(int i = 0; i < entryList.getLength(); i++) 
		{
			Node serviceNode = entryList.item(i);
			Element serviceElement = (Element) serviceNode;

			for(int j = 0; j < fields.size(); j++)
			{
				Element valueElement = (Element) serviceElement.getElementsByTagName(fields.get(j)).item(0);

				if(valueElement != null)
				{
					rows[i][j] = valueElement.getTextContent();
				}
				else
				{
					rows[i][j] = null;
				}
			}
		}


		logger.info("Received: " + entryList.getLength() + " rows");


		this.fireTableStructureChanged();

		this.fireTableDataChanged();
	}

	private void requestSupportedFields(String url) throws Exception
	{
		// request
		Document doc = http.requestXml(Http.GET, url + "/@supportedFields");


		NodeList itemList = doc.getElementsByTagName("item");
		
		for(int i = 0; i < itemList.getLength(); i++) 
		{
			Node itemNode = itemList.item(i);
			Element itemElement = (Element) itemNode;
			
			if(itemElement != null)
			{
				this.supportedFields.add(itemElement.getTextContent());
			}
		}
		
		logger.info("Found " + this.supportedFields.size() + " supported fields");
	}
}
