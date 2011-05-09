package com.k42b3.zubat;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
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

public class ZubatListModel extends AbstractListModel
{
	private Logger logger;

	private ArrayList<ServiceItem> services = new ArrayList<ServiceItem>();

	public ZubatListModel(String baseUrl) throws Exception
	{
		this.logger = Logger.getLogger("com.k42b3.zubat");

		this.request(baseUrl + "api/meta/xrds");
	}

	public Object getElementAt(int index) 
	{
		return services.get(index);
	}

	public int getSize() 
	{
		return services.size();
	}

	public String getUri(String type)
	{
		for(int i = 0; i < services.size(); i++)
		{
			if(services.get(i).getType().equals(type))
			{
				return services.get(i).getUri();
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

		HttpGet getRequest = new HttpGet(url);

		Zubat.getOauth().signRequest(getRequest);

		logger.info("Request: " + getRequest.getRequestLine());

		HttpResponse httpResponse = httpClient.execute(getRequest);

		HttpEntity entity = httpResponse.getEntity();


		// parse xrds
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(entity.getContent());

		Element rootElement = (Element) doc.getDocumentElement();

		rootElement.normalize();

		NodeList serviceList = doc.getElementsByTagName("Service");

		for(int i = 0; i < serviceList.getLength(); i++) 
		{
			Node serviceNode = serviceList.item(i);
			Element serviceElement = (Element) serviceNode;

			Element typeElement = (Element) serviceElement.getElementsByTagName("Type").item(0);
			Element uriElement = (Element) serviceElement.getElementsByTagName("URI").item(0);

			if(typeElement != null && uriElement != null)
			{
				String type = typeElement.getTextContent();
				String uri = uriElement.getTextContent();

				services.add(new ServiceItem(type, uri));
			}
		}

		logger.info("Found " + services.size() + " services");
	}
}
