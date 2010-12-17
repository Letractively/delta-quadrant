/**
 * metapod
 * 
 * An application with that you can train your macro for SC2 by setting up
 * hotkeys and starting a training mode where you must build specific units
 * in a period of time.
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of espeon. espeon is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * espeon is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with espeon. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.metapod;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

/**
 * Options
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Options extends JFrame
{
	private ArrayList<OptionListener> listener = new ArrayList<OptionListener>();
	private JList list;
	private ObjectListModel lm;
	
	public Options(Race race)
	{
		this.setTitle("metapod");

		this.setLocation(100, 100);

		this.setSize(180, 256);

		this.setMinimumSize(this.getSize());

		this.setResizable(false);

		this.setLayout(new BorderLayout());


		this.list = new JList();
		
		this.lm = new ObjectListModel(race);

		this.list.setModel(this.lm);
	
		this.list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		this.list.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));


		this.add(this.list, BorderLayout.CENTER);
		
		
		JPanel buttons = new JPanel();
		
		FlowLayout buttons_layout = new FlowLayout();

		buttons_layout.setAlignment(FlowLayout.LEFT);

		buttons.setLayout(buttons_layout);
		
		
		JButton btn_ok = new JButton("Save");
		
		btn_ok.setPreferredSize(new Dimension(80, 24));

		btn_ok.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				save();
			}
			
		});
		
		buttons.add(btn_ok);

		
		JButton btn_cancel = new JButton("Cancel");
		
		btn_cancel.setPreferredSize(new Dimension(80, 24));

		btn_cancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				close();
			}
			
		});
		
		buttons.add(btn_cancel);
		
		
		this.add(buttons, BorderLayout.SOUTH);
	}
	
	public void registerOptionListener(OptionListener ol)
	{
		this.listener.add(ol);
	}
	
	public void removeOptionListener(OptionListener ol)
	{
		this.listener.remove(ol);
	}

	public void clearOptionListener()
	{
		this.listener.clear();
	}

	public void save()
	{
		ArrayList<AbstractObject> objects = new ArrayList<AbstractObject>();
		
		int indices[] = this.list.getSelectedIndices();
		
		for(int i = 0; i < indices.length; i++)
		{
			objects.add(this.lm.getElementAt(indices[i]));
		}

		for(int i = 0; i < this.listener.size(); i++)
		{
			this.listener.get(i).notify(objects);
		}

		this.close();
	}

	public void close()
	{
		this.setVisible(false);
	}
}
