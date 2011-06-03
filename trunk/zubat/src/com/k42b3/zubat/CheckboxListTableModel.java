package com.k42b3.zubat;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CheckboxListTableModel extends AbstractTableModel
{
	protected String url;
	protected Http http;
	protected Logger logger;

	protected Object[][] rows;

	protected int totalResults;
	protected int startIndex;
	protected int itemsPerPage;

	public CheckboxListTableModel(String url, Http http) throws Exception 
	{
		this.url = url;
		this.http = http;
		this.logger = Logger.getLogger("com.k42b3.zubat");
		
		this.request(url);
	}

	public int getColumnCount()
	{
		return 2;
	}

	public String getColumnName(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0:

				return "Checked";

			case 1:

				return "Title";

			default:

				return null;
		}
	}

	public int getRowCount() 
	{
		return rows.length;
	}

	public Class getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0:

				return Boolean.class;

			default:

				return String.class;
		}
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
		rows = new Object[entry.getLength()][3];


		// parse entries
		NodeList entryList = doc.getElementsByTagName("entry");

		for(int i = 0; i < entryList.getLength(); i++) 
		{
			Node serviceNode = entryList.item(i);
			Element serviceElement = (Element) serviceNode;

			Element idElement = (Element) serviceElement.getElementsByTagName("id").item(0);
			Element titleElement = (Element) serviceElement.getElementsByTagName("title").item(0);
			Element checkedElement = (Element) serviceElement.getElementsByTagName("checked").item(0);

			if(idElement != null && titleElement != null && checkedElement != null)
			{
				rows[i][0] = Boolean.parseBoolean(checkedElement.getTextContent());
				rows[i][1] = titleElement.getTextContent();
				rows[i][2] = Integer.parseInt(idElement.getTextContent());
			}
		}


		logger.info("Received: " + entryList.getLength() + " rows");


		this.fireTableStructureChanged();

		this.fireTableDataChanged();
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if(rowIndex >= 0 && rowIndex < rows.length)
		{
			if(columnIndex >= 0 && columnIndex < rows[rowIndex].length)
			{
				rows[rowIndex][columnIndex] = aValue;
			}
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columnIndex == 0;
	}
}
