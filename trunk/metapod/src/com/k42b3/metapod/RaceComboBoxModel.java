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

import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 * RaceComboBoxModel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class RaceComboBoxModel implements ComboBoxModel
{
	private ArrayList<Race> races;
	private Race selected;
	private ArrayList<ListDataListener> listener = new ArrayList<ListDataListener>();

	public RaceComboBoxModel(ArrayList<Race> races)
	{
		this.races = races;
	}

	public Race getElementAt(int index) 
	{
		if(index >= 0 && index < this.races.size())
		{
			return this.races.get(index);
		}
		else
		{
			return null;
		}
	}

	public int getSize() 
	{
		return this.races.size();
	}

	public Race getSelectedItem() 
	{
		return this.selected;
	}

	public void setSelectedItem(Object object) 
	{		
		for(int i = 0; i < this.races.size(); i++)
		{
			if(this.races.get(i).equals(object))
			{
				this.selected = this.races.get(i);
			}
		}
	}
	
	public void addListDataListener(ListDataListener ldl) 
	{
		this.listener.add(ldl);
	}

	public void removeListDataListener(ListDataListener ldl) 
	{
		this.listener.remove(ldl);
	}
}
