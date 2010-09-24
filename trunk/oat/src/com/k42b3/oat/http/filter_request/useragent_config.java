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

package com.k42b3.oat.http.filter_request;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListDataListener;

import com.k42b3.oat.config_filter;

/**
 * useragent_config
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class useragent_config extends config_filter
{
	private JCheckBox ckb_active;
	private JComboBox cbo_agent;
	
	public String get_name()
	{
		return "User agent";
	}
	
	public Properties on_save() 
	{
		Properties props = new Properties();
		
		Object item = this.cbo_agent.getSelectedItem();
		
		if(item != null)
		{
			props.setProperty("agent", ((agent_entry) item).get_value());
		}

		return props;
	}
	
	public boolean is_active()
	{
		return this.ckb_active.isSelected();
	}

	public useragent_config()
	{
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JPanel panel = new JPanel();
		
		panel.setLayout(new GridLayout(0, 1));
		
		
		// active
		JPanel panel_active = new JPanel();

		panel_active.setLayout(new FlowLayout());


		JLabel lbl_active = new JLabel("Active:");

		lbl_active.setPreferredSize(new Dimension(100, 24));

		panel_active.add(lbl_active);


		this.ckb_active = new JCheckBox();
		
		this.ckb_active.setPreferredSize(new Dimension(200, 24));
		
		panel_active.add(this.ckb_active);
		
		
		panel.add(panel_active);
		
		
		// agent
		JPanel panel_agent = new JPanel();

		panel_agent.setLayout(new FlowLayout());


		JLabel lbl_agent = new JLabel("User agent:");

		lbl_agent.setPreferredSize(new Dimension(100, 24));

		panel_agent.add(lbl_agent);


		this.cbo_agent = new JComboBox();
		
		this.cbo_agent.setPreferredSize(new Dimension(200, 24));

		panel_agent.add(this.cbo_agent);


		panel.add(panel_agent);


		this.add(panel);
		
		
		// add agents
		ArrayList<agent_entry> agents = new ArrayList<agent_entry>();
		
		agents.add(new agent_entry("Firefox 4.0", "Mozilla/5.0 (Windows; U; Windows NT 6.1; ru; rv:1.9.2.3) Gecko/20100401 Firefox/4.0 (.NET CLR 3.5.30729)"));
		agents.add(new agent_entry("Firefox 3.8", "Mozilla/5.0 (X11; U; Linux i686; pl-PL; rv:1.9.0.2) Gecko/20121223 Ubuntu/9.25 (jaunty) Firefox/3.8"));
		agents.add(new agent_entry("Internet Explorer 8", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.2; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)"));
		agents.add(new agent_entry("Internet Explorer 7", "Mozilla/5.0 (Windows; U; MSIE 7.0; Windows NT 6.0; en-US)"));
		agents.add(new agent_entry("Internet Explorer 6", "Mozilla/5.0 (Windows; U; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)"));
		agents.add(new agent_entry("Lynx 2.8.7", "Lynx/2.8.7dev.4 libwww-FM/2.14 SSL-MM/1.4.1 OpenSSL/0.9.8d"));
		agents.add(new agent_entry("Opera 9.70", "Opera/9.70 (Linux i686 ; U; en) Presto/2.2.1"));
		agents.add(new agent_entry("Safari 5.0", "Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10_5_8; ja-jp) AppleWebKit/533.16 (KHTML, like Gecko) Version/5.0 Safari/533.16"));
		agents.add(new agent_entry("Safari 4.1", "Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10_4_11; nl-nl) AppleWebKit/533.16 (KHTML, like Gecko) Version/4.1 Safari/533.16"));
		agents.add(new agent_entry("Konqueror 4.4", "Mozilla/5.0 (compatible; Konqueror/4.4; Linux) KHTML/4.4.1 (like Gecko) Fedora/4.4.1-1.fc12"));
		agents.add(new agent_entry("Googlebot 2.1", "Googlebot/2.1 (+http://www.googlebot.com/bot.html)"));
		
		this.cbo_agent.setModel(new agent_model(agents));
	}

	class agent_model implements ComboBoxModel
	{
		ArrayList<agent_entry> agents = new ArrayList<agent_entry>();
		ArrayList<ListDataListener> listener = new ArrayList<ListDataListener>();
		
		private Object selected;

		public agent_model(ArrayList<agent_entry> agents)
		{
			this.agents.addAll(agents);
		}

		public agent_model()
		{
			this(null);
		}

		public Object getSelectedItem() 
		{
			return this.selected;
		}

		public void setSelectedItem(Object obj) 
		{
			this.selected = obj;
		}

		public void addListDataListener(ListDataListener l) 
		{
			this.listener.add(l);
		}

		public Object getElementAt(int index) 
		{
			return this.agents.get(index);
		}

		public int getSize() 
		{
			return this.agents.size();
		}

		public void removeListDataListener(ListDataListener l) 
		{
			this.listener.remove(l);
		}
	}
	
	class agent_entry
	{
		private String key;
		private String value;
		
		public agent_entry(String key, String value)
		{
			this.key = key;
			this.value = value;
		}
		
		public String get_key()
		{
			return this.key;
		}
		
		public String get_value()
		{
			return this.value;
		}
		
		public String toString()
		{
			return this.get_key();
		}
	}
}
