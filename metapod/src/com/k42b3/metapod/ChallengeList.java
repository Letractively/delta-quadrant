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

/**
 * ChallengeList
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class ChallengeList extends ArrayList<Challenge>
{
	private int solve_time;

	public ChallengeList(int solve_time)
	{
		this.solve_time = solve_time;
	}

	public void do_action(AbstractItem item)
	{
		for(int i = 0; i < this.size(); i++)
		{
			if(this.get(i).getItem().getName().equals(item.getName()))
			{
				this.get(i).decrement();
			}
		}
	}

	public int get_solve_time()
	{
		return this.solve_time;
	}

	public boolean is_solved()
	{
		for(int i = 0; i < this.size(); i++)
		{
			if(!this.get(i).is_solved())
			{
				return false;
			}
		}

		return true;
	}
	
	public int get_solved_challenges()
	{
		int s = 0;

		for(int i = 0; i < this.size(); i++)
		{
			if(this.get(i).is_solved())
			{
				s++;
			}
		}

		return s;
	}
}
