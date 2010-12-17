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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * Dashboard
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Dashboard extends JPanel
{
	private JComboBox cbo_race;
	private RaceComboBoxModel rm;
	private Inventory inventory;
	private ArrayList<RaceSelectionListener> listener = new ArrayList<RaceSelectionListener>();

	public Dashboard(ArrayList<Race> races)
	{
		this.setLayout(new BorderLayout());


		// race
		this.cbo_race = new JComboBox();

		this.rm = new RaceComboBoxModel(races);

		this.cbo_race.setModel(this.rm);

		this.cbo_race.addActionListener(new race_handler());

		this.cbo_race.setFocusable(false);


		this.add(this.cbo_race, BorderLayout.NORTH);


		this.inventory = new Inventory();

		this.inventory.setLayout(new GridLayout(0, 6));

		this.add(this.inventory, BorderLayout.CENTER);


		this.cbo_race.setSelectedIndex(0);
	}

	public void registerSelectionListener(SelectionListener sl)
	{
		this.inventory.registerSelectionListener(sl);
	}
	
	public void removeSelectionListener(SelectionListener sl)
	{
		this.inventory.removeSelectionListener(sl);
	}

	public void clearSelectioListener()
	{
		this.inventory.clearSelectioListener();
	}

	public void registerRaceSelectionListener(RaceSelectionListener rsl)
	{
		this.listener.add(rsl);
	}
	
	public void removeRaceSelectionListener(RaceSelectionListener rsl)
	{
		this.listener.remove(rsl);
	}

	public void clearRaceSelectioListener()
	{
		this.listener.clear();
	}

	public Race getSelectedRace()
	{
		return RaceFactory.getRace(cbo_race.getSelectedItem().toString());
	}

	public void setObjects(ArrayList<AbstractObject> objects)
	{
		this.inventory.setObjects(rm.getSelectedItem().get_objects());

		this.inventory.repaint();
	}

	class race_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				// set objects
				setObjects(rm.getSelectedItem().get_objects());


				// notify listener
				for(int i = 0; i < listener.size(); i++)
				{
					listener.get(i).notify(rm.getSelectedItem());
				}
			}
			catch(Exception ex)
			{
			}
		}
	}
}
