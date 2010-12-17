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

import javax.swing.DefaultListModel;

/**
 * ObjectListModel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class ObjectListModel extends DefaultListModel
{
	private Race race;
	
	public ObjectListModel(Race race)
	{
		this.race = race;
		
		ArrayList<AbstractObject> objects = this.race.get_objects();
		
		for(int i = 0; i < objects.size(); i++)
		{
			this.addElement(objects.get(i));
		}
	}
	
	public AbstractObject getElementAt(int index)
	{
		Object object = super.getElementAt(index);

		if(object instanceof AbstractObject)
		{
			return (AbstractObject) object;			
		}
		else
		{
			return null;
		}
	}
}
