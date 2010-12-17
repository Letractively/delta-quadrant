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
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Race
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public abstract class Race 
{
	protected ArrayList<AbstractObject> objects = new ArrayList<AbstractObject>();
	protected ArrayList<AbstractObject> deselected_objects = new ArrayList<AbstractObject>();

	public ArrayList<AbstractObject> get_objects() 
	{
		ArrayList<AbstractObject> objects = new ArrayList<AbstractObject>();

		for(int i = 0; i < this.objects.size(); i++)
		{
			if(!this.deselected_objects.contains(this.objects.get(i)))
			{
				objects.add(this.objects.get(i));
			}
		}

		return objects;
	}

	public void deselect_objects(ArrayList<AbstractObject> objects)
	{
		this.deselected_objects = objects;
	}

	public void clear()
	{
		this.deselected_objects.clear();
	}

	public ArrayList<AbstractItem> get_all_items()
	{
		ArrayList<AbstractObject> objects = this.get_objects();
		ArrayList<AbstractItem> items = new ArrayList<AbstractItem>();

		for(int i = 0; i < objects.size(); i++)
		{
			Iterator<Entry<Integer, AbstractItem>> it = objects.get(i).getItems().entrySet().iterator();

			while(it.hasNext())
			{
				Entry<Integer, AbstractItem> entry = (Entry<Integer, AbstractItem>) it.next();

				items.add(entry.getValue());
			}
		}

		return items;
	}

	public String toString()
	{
		return this.get_name();
	}

	abstract public String get_name();
}
