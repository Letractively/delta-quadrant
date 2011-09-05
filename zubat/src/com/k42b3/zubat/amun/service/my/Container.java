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

import com.k42b3.zubat.Http;
import com.k42b3.zubat.ServiceItem;

public class Container extends JPanel implements com.k42b3.zubat.Container
{
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
