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
import java.util.Random;

import javax.swing.JOptionPane;

/**
 * ChallengeGenerator
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class ChallengeGenerator 
{
	private Race race;

	public ChallengeGenerator(Race race)
	{
		this.race = race;
	}

	public ChallengeList generate(int time, int solving_time, int solved, int failed, int complete)
	{
		// get all available items
		ArrayList<AbstractItem> items = this.race.get_all_items();


		// get some random items from the complete list
		items = get_random_items(items, this.generate_random_count());


		// calculate how long the user has to solve these challenges in ms
		// based on the percent solving rate
		int rate = complete > 0 ? solved * 100 / complete : 0;

		if(rate >= 80)
		{
			// decrease the solving time by 10%
			solving_time = (int) (solving_time - (solving_time * 0.10));
		}
		else if(rate >= 60)
		{
			// decrease the solving time by 5%
			solving_time = (int) (solving_time - (solving_time * 0.5));
		}
		else if(rate >= 40)
		{
			// nothing changes
		}
		else if(rate >= 20)
		{
			// increase solving time by 5%
			solving_time = (int) (solving_time + (solving_time * 0.5));
		}
		else
		{
			// increase solving time by 10%
			solving_time = (int) (solving_time + (solving_time * 0.10));
		}


		// create the challenge list
		ChallengeList challenges = new ChallengeList(solving_time);


		for(int i = 0; i < items.size(); i++)
		{
			challenges.add(new Challenge(items.get(i), this.generate_random_count()));
		}
		

		return challenges;
	}

	private int generate_random_count()
	{
		Random generator = new Random();

		return generator.nextInt(4) + 1;
	}

	private ArrayList<AbstractItem> get_random_items(ArrayList<AbstractItem> items, int count)
	{
		ArrayList<AbstractItem> rnd_items = new ArrayList<AbstractItem>();
		Random generator = new Random();

		if(items.size() > 0)
		{
			for(int i = 0; i < count; i++)
			{
				int n = generator.nextInt(items.size());

				rnd_items.add(items.get(n));
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Found not enough challenges", "Error", JOptionPane.ERROR_MESSAGE);
		}

		return rnd_items;
	}
}
