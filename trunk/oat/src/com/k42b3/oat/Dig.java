/**
 * oat
 * 
 * An application with that you can make raw http requests to any url. You can 
 * save a request for later use. The application uses the java nio library to 
 * make non-blocking requests so the requests should work fluently.
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
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

package com.k42b3.oat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Type;

/**
 * Dig
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 165 $
 */
public class Dig extends JFrame
{
	private Url url;
	private Out out;
	private HashMap<Integer, String> types;
	private JLabel lblStatus;

	public Dig()
	{
		// settings
		this.setTitle("oat " + Oat.VERSION);
		this.setLocation(100, 100);
		this.setSize(500, 400);
		this.setMinimumSize(this.getSize());
		this.setLayout(new BorderLayout());


		// url
		JPanel panelUrl = new JPanel();
		panelUrl.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		panelUrl.setLayout(new BorderLayout());

		this.url = new Url();
		this.url.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) 
			{
			}

			public void keyReleased(KeyEvent e) 
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					lookup();
				}
			}

			public void keyPressed(KeyEvent e) 
			{
			}

		});
		this.url.setPreferredSize(new Dimension(200, 24));

		panelUrl.add(this.url, BorderLayout.CENTER);

		this.add(panelUrl, BorderLayout.NORTH);


		// textfield
		this.out = new Out();

		JScrollPane scp = new JScrollPane(this.out);
		scp.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));

		this.add(scp, BorderLayout.CENTER);


		// buttons
		JPanel panelButtons = new JPanel();

		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);

		panelButtons.setLayout(fl);

		JButton btnLookup = new JButton("Lookup");
		btnLookup.setMnemonic(java.awt.event.KeyEvent.VK_L);
		btnLookup.addActionListener(new lookupHandler());

		lblStatus = new JLabel("-");

		panelButtons.add(btnLookup);
		panelButtons.add(lblStatus);
		
		this.add(panelButtons, BorderLayout.SOUTH);


		types = new HashMap<Integer, String>();

		types.put(Type.A, "A (IPv4 address)");
		types.put(Type.NS, "NS (Name server)");
		types.put(Type.MD, "MD (Mail destination)");
		types.put(Type.MF, "MF (Mail forwarder)");
		types.put(Type.CNAME, "CNAME (Canonical name)");
		types.put(Type.SOA, "SOA (Start of authority)");
		types.put(Type.MB, "MB (Mailbox domain name)");
		types.put(Type.MG, "MG (Mail group member)");
		types.put(Type.MR, "MR (Mail rename name)");
		types.put(Type.NULL, "NULL (Null record)");
		types.put(Type.WKS, "WKS (Well known services)");
		types.put(Type.PTR, "PTR (Domain name pointer)");
		types.put(Type.HINFO, "HINFO (Host information)");
		types.put(Type.MINFO, "MINFO (Mailbox information)");
		types.put(Type.MX, "MX (Mail routing information)");
		types.put(Type.TXT, "TXT (Text strings)");
		types.put(Type.RP, "RP (Responsible person)");
		types.put(Type.GPOS, "GPOS (Geographical position)");
		types.put(Type.AAAA, "AAAA (IPv6 address)");
		types.put(Type.LOC, "LOC (Location)");
	}

	private void lookup()
	{
		out.setText("");
		lblStatus.setText("-");

		try
		{
			Iterator<Integer> keys = types.keySet().iterator();

			ExecutorService service = Executors.newSingleThreadExecutor();

			while(keys.hasNext())
			{
				int key = keys.next();

				service.submit(new requestWorker(key, types.get(key)));
			}

			service.shutdown();
		}
		catch(Exception ex)
		{
			out.append(ex.getMessage() + "\n");
		}
	}

	class requestWorker implements Runnable
	{
		private int type;
		private String desc;

		public requestWorker(int type, String desc)
		{
			this.type = type;
			this.desc = desc;
		}

		public void run()
		{
			lblStatus.setText("Request " + desc);

			StringBuilder result = new StringBuilder();

			result.append("> " + desc + "\n");

			try
			{
				SimpleResolver res = new SimpleResolver();

				Name name = Name.fromString(url.getText(), Name.root);
				Record rec = Record.newRecord(name, type, DClass.IN);

				Message query = Message.newQuery(rec);
				Message response = res.send(query);

				Record [] records = response.getSectionArray(Section.ANSWER);

				if(records.length > 0)
				{
					for(int j = 0; j < records.length; j++)
					{
						result.append(records[j] + "\n");
					}
				}
				else
				{
					return;
				}

				result.append("\n");
			}
			catch(Exception e)
			{
				result.append(e.getMessage() + "\n");
			}

			out.append(result.toString());
		}
	}

	public class lookupHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			lookup();
		}
	}
}
