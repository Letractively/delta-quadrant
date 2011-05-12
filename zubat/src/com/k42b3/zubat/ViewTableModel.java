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

import java.io.StringReader;
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
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
	private Oauth oauth;
	private String url;
	private TrafficListenerInterface trafficListener;
	private Logger logger;

	private ArrayList<String> supportedFields = new ArrayList<String>();
	private ArrayList<String> fields = new ArrayList<String>();
	private Object[][] rows;

	private int totalResults;
	private int startIndex;
	private int itemsPerPage;

	public ViewTableModel(Oauth oauth, String url, TrafficListenerInterface trafficListener) throws Exception
	{
		this.oauth = oauth;
		this.url = url;
		this.trafficListener = trafficListener;
		this.logger = Logger.getLogger("com.k42b3.zubat");

		this.requestSupportedFields(url);
	}

	public ViewTableModel(Oauth oauth, String url) throws Exception
	{
		this(oauth, url, null);
	}

	public void loadData(ArrayList<String> fields) throws Exception
	{
		this.fields = fields;

		this.request(url);
	}

	public void nextPage() throws Exception
	{
		int index = startIndex + itemsPerPage;

		String url = Zubat.appendQuery(this.url, "count=" + itemsPerPage + "&startIndex=" + index);

		this.request(url);
	}

	public void prevPage() throws Exception
	{
		int index = startIndex - itemsPerPage;
		index = index < 0 ? 0 : index;

		String url = Zubat.appendQuery(this.url, "count=" + itemsPerPage + "&startIndex=" + index);

		this.request(url);
	}

	public ArrayList<String> getSupportedFields()
	{
		return this.supportedFields;
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
		// build request
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

		StringBuilder queryFields = new StringBuilder();

		for(int i = 0; i < fields.size(); i++)
		{
			if(this.supportedFields.contains(fields.get(i)))
			{
				queryFields.append(fields.get(i) + ",");
			}
		}
		
		url = Zubat.appendQuery(url, "fields=" + queryFields.substring(0, queryFields.length() - 1));

		HttpGet getRequest = new HttpGet(url);

		getRequest.addHeader("Accept", "application/xml");

		oauth.signRequest(getRequest);

		logger.info("Request: " + getRequest.getRequestLine());

		HttpResponse httpResponse = httpClient.execute(getRequest);

		HttpEntity entity = httpResponse.getEntity();

		String responseContent = EntityUtils.toString(entity);


		// log traffic
		if(trafficListener != null)
		{
			TrafficItem trafficItem = new TrafficItem();

			trafficItem.setRequest(getRequest);
			trafficItem.setResponse(httpResponse);
			trafficItem.setResponseContent(responseContent);

			trafficListener.handleRequest(trafficItem);
		}


		// parse response
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(responseContent));

		Document doc = db.parse(is);

		Element rootElement = (Element) doc.getDocumentElement();

		rootElement.normalize();

		
		// get message
		Message msg = Zubat.parseResponse(rootElement);

		if(!msg.hasSuccess())
		{
			throw new Exception("API error occured");
		}


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

		rows = new Object[rowSize][fields.size()];


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


		this.fireTableDataChanged();
	}

	private void requestSupportedFields(String url) throws Exception
	{
		// build request
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

		HttpGet getRequest = new HttpGet(url + "/@supportedFields");

		getRequest.addHeader("Accept", "application/xml");

		oauth.signRequest(getRequest);

		logger.info("Request: " + getRequest.getRequestLine().toString());

		HttpResponse httpResponse = httpClient.execute(getRequest);

		HttpEntity entity = httpResponse.getEntity();

		String responseContent = EntityUtils.toString(entity);


		// log traffic
		if(trafficListener != null)
		{
			TrafficItem trafficItem = new TrafficItem();

			trafficItem.setRequest(getRequest);
			trafficItem.setResponse(httpResponse);
			trafficItem.setResponseContent(responseContent);

			trafficListener.handleRequest(trafficItem);
		}


		// parse response
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(responseContent));

		Document doc = db.parse(is);

		Element rootElement = (Element) doc.getDocumentElement();

		rootElement.normalize();


		// get message
		Message msg = Zubat.parseResponse(rootElement);

		if(!msg.hasSuccess())
		{
			throw new Exception("API error occured");
		}


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
