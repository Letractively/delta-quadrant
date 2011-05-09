package com.k42b3.zubat;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableModel;
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

import com.k42b3.zubat.oauth.OauthProvider;

public class Zubat extends JFrame
{
	public static String version = "0.0.1 beta";

	private static Oauth oauth;

	private String baseUrl;
	private HashMap<String, String> services = new HashMap<String, String>();

	private Logger logger;

	public Zubat()
	{
		logger = Logger.getLogger("com.k42b3.zubat");
		
		this.setTitle("zubat (version: " + Zubat.version + ")");

		this.setLocation(100, 100);

		this.setSize(600, 400);

		this.setMinimumSize(this.getSize());

		this.setLayout(new BorderLayout());

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		// @todo this should com frome an config file
		baseUrl = "http://127.0.0.1/projects/amun/public/index.php/";

		String consumerKey = "b8858501073e5fb54e75b973ed044ec19f21a60d";
		String consumerSecret = "07d8b5173afba2575e57ca5966624a39419b5b70";
		String token = "cec314dc235df67c67b5c6b389b12102817135b2";
		String tokenSecret = "5f9b76722e47b03bee7a91afb4946320b3ed4a28";

		String request = "http://127.0.0.1/projects/amun/public/index.php/api/auth/request";
		String authorization = "http://127.0.0.1/projects/amun/public/index.php/api/auth/authorization";
		String access = "http://127.0.0.1/projects/amun/public/index.php/api/auth/access";


		// create provider
		OauthProvider provider = new OauthProvider(request, authorization, access, consumerKey, consumerSecret);
		oauth = new Oauth(provider);

		if(!token.isEmpty() && !tokenSecret.isEmpty())
		{
			oauth.setToken(token);
			oauth.setTokenSecret(tokenSecret);

			loadServices();

			setVisible(true);
		}
		else
		{
			new Login().setCallback(new CallbackInterface() {

				public void call()
				{
					loadServices();

					setVisible(true);
				}

			});
		}
	}

	public void loadTable(String uri)
	{
		try
		{
			String[] fields = {"id", "parentId", "application", "url", "title", "template", "date"};

			ZubatTablelModel tm = new ZubatTablelModel(baseUrl + "api/content/page", fields);
			JTable table = new JTable(tm);

			this.add(table, BorderLayout.CENTER);

			this.doLayout();
		}
		catch(Exception e)
		{
			logger.info(e.getMessage());
		}
	}

	public void loadServices()
	{
		try
		{
			ZubatListModel lm = new ZubatListModel(baseUrl);
			JList list = new JList(lm);

			ServiceItem item = (ServiceItem) lm.getElementAt(0);

			lm.getUri(baseUrl + "/");

			if(item != null)
			{
				this.loadTable(item.getUri());
			}

			this.add(list, BorderLayout.NORTH);
		}
		catch(Exception e)
		{
			logger.info(e.getMessage());
		}
	}

	public static Oauth getOauth()
	{
		return oauth;
	}
}
