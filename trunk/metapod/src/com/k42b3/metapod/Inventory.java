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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * Inventory
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class Inventory extends JPanel implements MouseListener
{
	private ArrayList<AbstractObject> objects = new ArrayList<AbstractObject>();
	private ArrayList<SelectionListener> listener = new ArrayList<SelectionListener>();
	private AbstractObject selected;

	public Inventory()
	{
		this.addMouseListener(this);
	}

	public void setObjects(ArrayList<AbstractObject> objects)
	{
		this.objects = objects;
	}

	public void clear()
	{
		this.objects.clear();
	}

	public AbstractObject getSelected()
	{
		return this.selected;
	}

	public void registerSelectionListener(SelectionListener sl)
	{
		this.listener.add(sl);
	}
	
	public void removeSelectionListener(SelectionListener sl)
	{
		this.listener.remove(sl);
	}

	public void clearSelectioListener()
	{
		this.listener.clear();
	}

	protected void paintComponent(Graphics g)
	{
		g.setColor(this.getBackground());
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		int x = 0, y = -26;

		for(int i = 0; i < this.objects.size(); i++)
		{
			Image img = metapod.get_image(metapod.iconBasePath + "/" + this.objects.get(i).getRace() + "/" + this.objects.get(i).getName() + ".png");

			if(img != null)
			{
				int width = img.getWidth(null);
				int height = img.getHeight(null);

				if(i % 3 == 0)
				{
					x = 0;
					y+= width;
				}
				else
				{
					x+= height;
				}

				this.objects.get(i).setX(x);
				this.objects.get(i).setY(y);
				this.objects.get(i).setWidth(width);
				this.objects.get(i).setHeight(height);

				if(this.selected == this.objects.get(i))
				{
					g.drawImage(img, x, y, this);
					g.setColor(Color.BLACK);
					g.drawRect(x, y, width - 1, height - 1);
				}
				else
				{
					g.drawImage(img, x, y, this);
				}
			}
		}
	}

	public void mouseClicked(MouseEvent e) 
	{
		this.selected = null;

		for(int i = 0; i < this.objects.size(); i++)
		{
			if(e.getX() >= this.objects.get(i).getX() && 
			e.getX() < this.objects.get(i).getX() + this.objects.get(i).getWidth() &&
			e.getY() >= this.objects.get(i).getY() &&
			e.getY() < this.objects.get(i).getY() + this.objects.get(i).getHeight())
			{
				this.selected = this.objects.get(i);
				
				for(int j = 0; j < this.listener.size(); j++)
				{
					this.listener.get(j).notify(this.selected);
				}
			}
		}
		
		if(this.selected != null)
		{
			this.repaint();
		}
	}

	public void mouseEntered(MouseEvent e) 
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e) 
	{
	}

	public void mouseReleased(MouseEvent e) 
	{
	}
}
