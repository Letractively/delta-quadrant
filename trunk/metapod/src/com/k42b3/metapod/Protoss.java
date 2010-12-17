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

import com.k42b3.metapod.protoss.CyberneticsCore;
import com.k42b3.metapod.protoss.DarkShrine;
import com.k42b3.metapod.protoss.FleetBeacon;
import com.k42b3.metapod.protoss.Forge;
import com.k42b3.metapod.protoss.Gateway;
import com.k42b3.metapod.protoss.Nexus;
import com.k42b3.metapod.protoss.RoboticsBay;
import com.k42b3.metapod.protoss.RoboticsFacility;
import com.k42b3.metapod.protoss.Stargate;
import com.k42b3.metapod.protoss.TemplarArchives;
import com.k42b3.metapod.protoss.TwilightCouncil;

/**
 * Protoss
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Protoss extends Race
{
	public Protoss()
	{
		objects.add(new CyberneticsCore());
		objects.add(new DarkShrine());
		objects.add(new FleetBeacon());
		objects.add(new Forge());
		objects.add(new Gateway());
		objects.add(new Nexus());
		objects.add(new RoboticsBay());
		objects.add(new RoboticsFacility());
		objects.add(new Stargate());
		objects.add(new TemplarArchives());
		objects.add(new TwilightCouncil());
	}

	public String get_name() 
	{
		return "Protoss";
	}
}
