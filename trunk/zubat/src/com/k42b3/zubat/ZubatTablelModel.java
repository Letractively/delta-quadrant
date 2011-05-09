package com.k42b3.zubat;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ZubatTablelModel extends AbstractTableModel
{
	private Logger logger;
	private String url;

	private ArrayList<String> supportedFields = new ArrayList<String>();
	private String[] fields;
	private Object[][] rows;

	private int totalResults;
	private int startIndex;
	private int itemsPerPage;

	public ZubatTablelModel(String url, String[] fields) throws Exception
	{
		this.logger = Logger.getLogger("com.k42b3.zubat");
		this.url = url;
		this.fields = fields;

		this.getSupportedFields(url);

		this.request(url);
	}

	public void nextPage() throws Exception
	{
		int index = (startIndex + itemsPerPage) <= totalResults ? startIndex + itemsPerPage : totalResults;

		this.request(url + "?count=" + itemsPerPage + "&startIndex=" + index);
	}

	public void prevPage() throws Exception
	{
		int index = (startIndex - itemsPerPage) > 0 ? startIndex - itemsPerPage : 0;

		this.request(url + "?count=" + itemsPerPage + "&startIndex=" + index);
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
		return fields.length;
	}

	public String getColumnName(int columnIndex)
	{
		return fields[columnIndex];
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
		// build request
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

		StringBuilder queryFields = new StringBuilder();

		for(int i = 0; i < fields.length; i++)
		{
			if(this.supportedFields.contains(fields[i]))
			{
				queryFields.append(fields[i] + ",");
			}
		}

		HttpGet getRequest = new HttpGet(url + "?fields=" + queryFields.substring(0, queryFields.length() - 1));

		getRequest.addHeader("Accept", "application/xml");

		Zubat.getOauth().signRequest(getRequest);

		logger.info("Request: " + getRequest.getRequestLine());

		HttpResponse httpResponse = httpClient.execute(getRequest);

		HttpEntity entity = httpResponse.getEntity();


		// parse response
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(entity.getContent());

		Element rootElement = (Element) doc.getDocumentElement();

		rootElement.normalize();


		// get meta
		Element totalResultsElement = (Element) doc.getElementsByTagName("totalResults").item(0);
		Element startIndexElement = (Element) doc.getElementsByTagName("startIndex").item(0);
		Element itemsPerPageElement = (Element) doc.getElementsByTagName("itemsPerPage").item(0);
		
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
		int rowSize = totalResults > itemsPerPage ? itemsPerPage : totalResults;

		rows = new Object[rowSize][fields.length];


		// parse entries
		NodeList entryList = doc.getElementsByTagName("entry");

		for(int i = 0; i < entryList.getLength(); i++) 
		{
			Node serviceNode = entryList.item(i);
			Element serviceElement = (Element) serviceNode;

			for(int j = 0; j < fields.length; j++)
			{
				Element valueElement = (Element) serviceElement.getElementsByTagName(fields[j]).item(0);

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
	}
	
	private void getSupportedFields(String url) throws Exception
	{
		// build request
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

		HttpGet getRequest = new HttpGet(url + "/@supportedFields");

		getRequest.addHeader("Accept", "application/xml");

		Zubat.getOauth().signRequest(getRequest);

		logger.info("Request: " + getRequest.getRequestLine());

		HttpResponse httpResponse = httpClient.execute(getRequest);

		HttpEntity entity = httpResponse.getEntity();


		// parse response
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(entity.getContent());

		Element rootElement = (Element) doc.getDocumentElement();

		rootElement.normalize();


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
