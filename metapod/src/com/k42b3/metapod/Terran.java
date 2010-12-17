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

import com.k42b3.metapod.terran.Armory;
import com.k42b3.metapod.terran.Barracks;
import com.k42b3.metapod.terran.CommandCenter;
import com.k42b3.metapod.terran.EngineeringBay;
import com.k42b3.metapod.terran.Factory;
import com.k42b3.metapod.terran.FusionCore;
import com.k42b3.metapod.terran.GhostAcademy;
import com.k42b3.metapod.terran.Starport;

/**
 * Terran
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Terran extends Race
{
	public Terran()
	{
		objects.add(new Armory());
		objects.add(new Barracks());
		objects.add(new CommandCenter());
		objects.add(new EngineeringBay());
		objects.add(new Factory());
		objects.add(new FusionCore());
		objects.add(new GhostAcademy());
		objects.add(new Starport());
	}

	public String get_name() 
	{
		return "Terran";
	}
}
