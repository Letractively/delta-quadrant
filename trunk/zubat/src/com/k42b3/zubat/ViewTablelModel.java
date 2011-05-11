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
public class ViewTablelModel extends AbstractTableModel
{
	private Oauth oauth;
	private String url;
	private Logger logger;

	private TrafficListenerInterface trafficListener;

	private ArrayList<String> supportedFields = new ArrayList<String>();
	private ArrayList<String> fields;
	private Object[][] rows;

	private int totalResults;
	private int startIndex;
	private int itemsPerPage;

	public ViewTablelModel(Oauth oauth, String url) throws Exception
	{
		this.oauth = oauth;
		this.url = url;
		this.logger = Logger.getLogger("com.k42b3.zubat");

		this.requestSupportedFields(url);
	}

	public ViewTablelModel(Oauth oauth, String url, TrafficListenerInterface trafficListener) throws Exception
	{
		this(oauth, url);

		this.trafficListener = trafficListener;
	}

	public void loadData(ArrayList<String> fields) throws Exception
	{
		this.fields = fields;

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

		HttpGet getRequest = new HttpGet(url + "?fields=" + queryFields.substring(0, queryFields.length() - 1));

		getRequest.addHeader("Accept", "application/xml");

		oauth.signRequest(getRequest);

		logger.info("Request: " + getRequest.getRequestLine());

		HttpResponse httpResponse = httpClient.execute(getRequest);

		HttpEntity entity = httpResponse.getEntity();


		// parse response
		String responseContent = Zubat.getEntityContent(entity);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(responseContent));

		Document doc = db.parse(is);

		Element rootElement = (Element) doc.getDocumentElement();

		rootElement.normalize();

		if(Zubat.hasError(rootElement))
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


		// log traffic
		if(trafficListener != null)
		{
			TrafficItem trafficItem = new TrafficItem();

			trafficItem.setRequest(getRequest);
			trafficItem.setResponse(httpResponse);
			trafficItem.setResponseContent(responseContent);

			trafficListener.handleRequest(trafficItem);
		}
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

		logger.info("Request: " + getRequest.getRequestLine());

		HttpResponse httpResponse = httpClient.execute(getRequest);

		HttpEntity entity = httpResponse.getEntity();


		System.out.println();
		
		// parse response
		String responseContent = Zubat.getEntityContent(entity);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(responseContent));

		Document doc = db.parse(is);

		Element rootElement = (Element) doc.getDocumentElement();

		rootElement.normalize();

		if(Zubat.hasError(rootElement))
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


		// log traffic
		if(trafficListener != null)
		{
			TrafficItem trafficItem = new TrafficItem();

			trafficItem.setRequest(getRequest);
			trafficItem.setResponse(httpResponse);
			trafficItem.setResponseContent(responseContent);

			trafficListener.handleRequest(trafficItem);
		}
	}
}
