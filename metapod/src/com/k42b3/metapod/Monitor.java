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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

/**
 * Monitor
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Monitor extends JPanel
{
	private HashMap<Integer, ArrayList<AbstractObject>> groups = new HashMap<Integer, ArrayList<AbstractObject>>();
	private int selected_group;
	private int selected_tab;
	private Race selected_race;
	private ChallengeList challenges;

	public void repaint(HashMap<Integer, ArrayList<AbstractObject>> groups, Race selected_race, int selected_group, int selected_tab)
	{
		this.groups = groups;
		this.selected_race = selected_race;
		this.selected_group = selected_group;
		this.selected_tab = selected_tab;

		this.repaint();
	}

	/**
	 * set_challenge
	 * 
	 * Set the challenge where the first key is the object wich is expected
	 * and the second key is the amount how often the object is needed.
	 * 
	 * @param challenge
	 */
	public void set_challenge(ChallengeList challenges)
	{
		this.challenges = challenges;
	}

	protected void paintComponent(Graphics g)
	{
		g.setColor(this.getBackground());
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.BLACK);

		this.draw_empty_map(g, 10, -28);
		this.draw_empty_map(g, 200, -28);

		if(this.groups.containsKey(this.selected_group))
		{
			// temp x, y
			int t_x;
			int t_y;


			ArrayList<AbstractObject> objects = this.groups.get(this.selected_group);

			t_x = 10;
			t_y = -28;

			for(int j = 0; j < 15; j++)
			{
				if(j % 5 == 0)
				{
					t_x = 10;
					t_y+= 34;
				}

				if(this.selected_tab == j)
				{
					g.setColor(Color.RED);
					g.fillRect(t_x + 3, t_y + 6, 35, 35);
					g.setColor(Color.BLACK);
				}

				if(j < objects.size() && objects.get(j) != null)
				{
					Image img = metapod.get_image(metapod.iconBasePath + "/" + objects.get(j).getRace() + "/" + objects.get(j).getName() + ".png");

					g.drawImage(img, t_x + 4, t_y + 8, this);
				}

				t_x+= 34;
			}
			
			
			// list items
			HashMap<Integer, AbstractItem> items = this.groups.get(this.selected_group).get(this.selected_tab).getItems();

			int selected_item = this.groups.get(this.selected_group).get(this.selected_tab).getSelectedItem();

			t_x = 200;
			t_y = -28;

			for(int j = 0; j < 15; j++)
			{
				if(j % 5 == 0)
				{
					t_x = 200;
					t_y+= 34;
				}

				if(items.containsKey(j))
				{
					if(selected_item == j)
					{
						g.setColor(Color.RED);
						g.fillRect(t_x + 3, t_y + 6, 35, 35);
						g.setColor(Color.BLACK);
					}

					Image img = metapod.get_image(metapod.iconBasePath + "/" + items.get(j).getRace() + "/items/" + items.get(j).getName() + ".png");

					g.drawImage(img, t_x + 4, t_y + 8, this);
				}

				t_x+= 34;
			}
		}


		// draw challenge
		g.setFont(new Font("Verdana", Font.BOLD, 14));
		
		if(this.challenges != null)
		{
			int t_x = 20;

			if(this.challenges.size() > 0)
			{
				for(int i = 0; i < this.challenges.size(); i++)
				{
					g.drawString(this.challenges.get(i).getCount() + "x ", t_x, 145);

					Image img = metapod.get_image(metapod.iconBasePath + "/" + this.challenges.get(i).getItem().getRace() + "/items/" + this.challenges.get(i).getItem().getName() + ".png");

					g.drawImage(img, t_x + 25, 125, this);
					
					
					t_x+= 70;
				}
			}
			else
			{
				g.drawString("Done!", 20, 145);
			}
		}
		else
		{
			g.drawString("Press start", 20, 145);
		}
	}

	private void draw_empty_map(Graphics g, int x, int y)
	{
		// temp x, y
		int t_x = x;
		int t_y = y;
		
		// draw rect
		g.setColor(Color.BLACK);
		g.drawRect(x, y + 38, 175, 108);

		for(int j = 0; j < 15; j++)
		{
			if(j % 5 == 0)
			{
				t_x = x;
				t_y+= 34;
			}

			Image img = metapod.get_image(metapod.iconBasePath + "/neutral/items/Empty.png");

			g.drawImage(img, t_x + 4, t_y + 8, this);

			t_x+= 34;
		}
	}
}
