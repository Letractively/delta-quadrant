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

import com.k42b3.metapod.zerg.BanelingNest;
import com.k42b3.metapod.zerg.EvolutionChamber;
import com.k42b3.metapod.zerg.GreaterSpire;
import com.k42b3.metapod.zerg.Hatchery;
import com.k42b3.metapod.zerg.Hive;
import com.k42b3.metapod.zerg.HydraliskDen;
import com.k42b3.metapod.zerg.InfestationPit;
import com.k42b3.metapod.zerg.Lair;
import com.k42b3.metapod.zerg.RoachWarren;
import com.k42b3.metapod.zerg.SpawningPool;
import com.k42b3.metapod.zerg.Spire;
import com.k42b3.metapod.zerg.UltraliskCavern;

/**
 * Zerg
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Zerg extends Race
{
	public Zerg()
	{
		objects = new ArrayList<AbstractObject>();

		objects.add(new BanelingNest());
		objects.add(new EvolutionChamber());
		objects.add(new Hatchery());
		objects.add(new Hive());
		objects.add(new HydraliskDen());
		objects.add(new InfestationPit());
		objects.add(new Lair());
		objects.add(new RoachWarren());
		objects.add(new SpawningPool());
		objects.add(new Spire());
		objects.add(new GreaterSpire());
		objects.add(new UltraliskCavern());
	}

	public String get_name() 
	{
		return "Zerg";
	}
}
