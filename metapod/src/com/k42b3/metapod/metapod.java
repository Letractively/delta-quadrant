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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * metapod
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class metapod extends JFrame
{
	public static String ver = "0.0.2 beta";
	public static String iconBasePath = "/images";

	private Toolbar toolbar;
	private Dashboard dashboard;
	private Monitor monitor;
	private APMStream apm_stream;

	private HashMap<Integer, ArrayList<AbstractObject>> groups = new HashMap<Integer, ArrayList<AbstractObject>>();
	private ArrayList<Race> races = new ArrayList<Race>();

	private int selected_group = -1;
	private int selected_tab = 0;
	private Race selected_race;
	private AbstractObject selected;

	private int apm = 0;
	private int time = 1;
	private int solving_time = 5000; // start with 5 seconds
	private int solved = 0;
	private int failed = 0;
	private int complete = 0;
	
	private Thread game_thread;
	private Thread apm_thread;
	
	private boolean is_running = false;

	private ChallengeList challenges;

	public metapod()
	{
		this.setTitle("metapod (version: " + ver + ")");

		this.setLocation(100, 100);

		this.setSize(512, 256);

		this.setMinimumSize(this.getSize());

		this.setResizable(false);


		// add races
		this.selected_race = new Terran();

		this.races.add(this.selected_race);
		this.races.add(new Protoss());
		//this.races.add(new Zerg());


		// apm stream
		this.apm_stream = new APMStream();
		this.apm_stream.setPreferredSize(new Dimension(50, 28));

		this.add(this.apm_stream, BorderLayout.NORTH);
		
		
		// dashboard
		this.dashboard = new Dashboard(this.races);
		this.dashboard.setPreferredSize(new Dimension(110, 50));
		this.dashboard.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		this.dashboard.registerSelectionListener(new SelectionListener(){

			public void notify(AbstractObject object)
			{
				selected = object;
			}

		});
		
		this.dashboard.registerRaceSelectionListener(new RaceSelectionListener(){

			public void notify(Race race)
			{
				groups.clear();

				selected_race = race;
				selected_group = -1;
				selected_tab = 0;


				monitor.repaint(groups, selected_race, selected_group, selected_tab);
			}

		});

		this.add(this.dashboard, BorderLayout.WEST);


		// monitor
		this.monitor = new Monitor();

		this.add(this.monitor, BorderLayout.CENTER);


		// toolbar
		this.toolbar = new Toolbar();

		this.toolbar.get_start().addActionListener(new start_handler());
		this.toolbar.get_options().addActionListener(new options_handler());
		this.toolbar.get_about().addActionListener(new about_handler());
		this.toolbar.get_exit().addActionListener(new exit_handler());

		this.getContentPane().add(this.toolbar, BorderLayout.SOUTH);
		
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setFocusTraversalKeysEnabled(false);

		this.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e)
			{
				// select groups
				if(e.getModifiers() == 2)
				{
					switch(e.getKeyCode())
					{
						case 49: create_group(1); break; // 1
						case 50: create_group(2); break; // 2
						case 51: create_group(3); break; // 3
						case 52: create_group(4); break; // 4
						case 53: create_group(5); break; // 5
						case 54: create_group(6); break; // 6
						case 55: create_group(7); break; // 7
						case 56: create_group(8); break; // 8
						case 57: create_group(9); break; // 9
						case 58: create_group(0); break; // 0
					}
				}
				else if(e.getModifiers() == 1)
				{
					switch(e.getKeyCode())
					{
						case 49: add_group(1); break; // 1
						case 50: add_group(2); break; // 2
						case 51: add_group(3); break; // 3
						case 52: add_group(4); break; // 4
						case 53: add_group(5); break; // 5
						case 54: add_group(6); break; // 6
						case 55: add_group(7); break; // 7
						case 56: add_group(8); break; // 8
						case 57: add_group(9); break; // 9
						case 58: add_group(0); break; // 0
					}
				}
				else
				{
					switch(e.getKeyCode())
					{
						case 49: select_group(1); break; // 1
						case 50: select_group(2); break; // 2
						case 51: select_group(3); break; // 3
						case 52: select_group(4); break; // 4
						case 53: select_group(5); break; // 5
						case 54: select_group(6); break; // 6
						case 55: select_group(7); break; // 7
						case 56: select_group(8); break; // 8
						case 57: select_group(9); break; // 9
						case 58: select_group(0); break; // 0
					}
				}
				
				// tab
				if(e.getKeyCode() == 9)
				{
					select_tab();
				}

				// get grid layout
				switch(e.getKeyCode())
				{
					case 81: do_action(0); break;
					case 87: do_action(1); break;
					case 69: do_action(2); break;
					case 82: do_action(3); break;
					case 84: do_action(4); break;
					
					case 65: do_action(5); break;
					case 83: do_action(6); break;
					case 68: do_action(7); break;
					case 70: do_action(8); break;
					case 71: do_action(9); break;

					case 89: do_action(10); break;
					case 88: do_action(11); break;
					case 67: do_action(12); break;
					case 86: do_action(13); break;
					case 66: do_action(14); break;
				}
			}

			public void keyReleased(KeyEvent e)
			{
			}

			public void keyTyped(KeyEvent e) 
			{
			}

		});

		this.setVisible(true);
		
		this.requestFocus();
	}

	private void create_group(int pos)
	{
		// create a new group with all selected units
		ArrayList<AbstractObject> objects = new ArrayList<AbstractObject>();

		AbstractObject object = this.get_selected_unit();
		
		if(object != null)
		{
			objects.add(this.get_selected_unit());

			this.groups.put(pos, objects);


			this.monitor.repaint(this.groups, this.selected_race, this.selected_group, this.selected_tab);
			
			apm++;
		}
	}

	private void add_group(int pos)
	{
		// if the group exists add the selected units to the group
		if(this.groups.containsKey(pos))
		{
			if(this.groups.get(pos).size() < 15)
			{
				this.groups.get(pos).add(this.get_selected_unit());


				this.monitor.repaint(this.groups, this.selected_race, this.selected_group, this.selected_tab);
				
				apm++;
			}
		}
	}

	private void select_group(int pos)
	{
		// select group if available
		this.selected_tab = 0;

		if(this.groups.containsKey(pos))
		{
			this.selected_group = pos;


			this.monitor.repaint(this.groups, this.selected_race, this.selected_group, this.selected_tab);

			apm++;
		}
		else
		{
			this.selected_group = -1;
		}
	}

	public void do_action(int pos)
	{
		if(this.get_selected_group() != null)
		{
			HashMap<Integer, AbstractItem> items = this.get_selected_group().get(this.selected_tab).getItems();

			if(items.containsKey(pos))
			{
				this.get_selected_group().get(this.selected_tab).setSelectedItem(pos);

				if(this.challenges != null)
				{
					this.challenges.do_action(this.get_selected_group().get(this.selected_tab).getItems().get(this.get_selected_group().get(this.selected_tab).getSelectedItem()));
				}


				this.monitor.repaint(this.groups, this.selected_race, this.selected_group, this.selected_tab);

				apm++;
			}
		}
	}
	
	private void select_tab()
	{
		if(this.get_selected_group() != null)
		{
			if(this.selected_tab >= this.get_selected_group().size() - 1)
			{
				this.selected_tab = 0;
			}
			else
			{
				this.selected_tab++;
			}
		}
		else
		{
			this.selected_tab = 0;
		}


		this.monitor.repaint(this.groups, this.selected_race, this.selected_group, this.selected_tab);

		apm++;
	}

	private ArrayList<AbstractObject> get_selected_group()
	{
		return this.groups.get(this.selected_group);
	}

	private AbstractObject get_selected_unit()
	{
		return this.selected;
	}

	public class game_handler implements Runnable
	{
		public void run()
		{
			ChallengeGenerator gen = new ChallengeGenerator(selected_race);

			while(is_running)
			{
				try
				{
					if(challenges != null)
					{
						if(challenges.is_solved())
						{
							solved+= challenges.size();
						}
						else
						{
							int s = challenges.get_solved_challenges();

							solved+= s;
							failed+= challenges.size() - s;
						}
					}

					challenges = gen.generate(time, solving_time, solved, failed, complete);

					complete+= challenges.size();

					monitor.set_challenge(challenges);

					solving_time = challenges.get_solve_time();
					
					Thread.sleep(solving_time);
				}
				catch(InterruptedException ie)
				{
				}
			}
		}
	}

	public class apm_handler implements Runnable
	{
		public void run() 
		{
			while(is_running)
			{
				try
				{
					time++;

					apm_stream.repaint(apm, time, solving_time, solved, failed);


					monitor.repaint(groups, selected_race, selected_group, selected_tab);


					Thread.sleep(750);
				}
				catch(InterruptedException ie)
				{
				}
			}
		}
	}

	public class start_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent args) 
		{
			if(!is_running)
			{
				is_running = true;

				apm = 0;

				game_thread = new Thread(new apm_handler());
				game_thread.start();

				apm_thread = new Thread(new game_handler());
				apm_thread.start();

				toolbar.get_start().setText("Stop");
			}
			else
			{
				is_running = false;

				toolbar.get_start().setText("Start");
			}
		}
	}

	public class options_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent args) 
		{
			SwingUtilities.invokeLater(new Runnable(){

				public void run() 
				{
					Options win = new Options(selected_race);
					
					win.registerOptionListener(new OptionListener(){

						public void notify(ArrayList<AbstractObject> objects) 
						{
							selected_race.deselect_objects(objects);

							dashboard.setObjects(objects);
						}

					});

					win.pack();

					win.setVisible(true);
				}

			});
		}
	}
	
	public class stop_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent args) 
		{
			is_running = false;
		}
	}

	public class about_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent args)
		{
			StringBuilder out = new StringBuilder();

			out.append("metabod version " + ver + "\n");
			out.append("Author: Christoph \"k42b3\" Kappestein" + "\n");
			out.append("Contact: k42b3.x@gmail.com" + "\n");
			out.append("\n");
			out.append("An application with that you can train your macro for SC2 by setting up" + "\n");
			out.append("hotkeys and starting a training mode where you must build specific units" + "\n");
			out.append("in a period of time." + "\n");

			JOptionPane.showMessageDialog(null, out, "About", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public class exit_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent args) 
		{
			System.exit(0);
		}
	}

	public static Image get_image(String path)
	{
		return Toolkit.getDefaultToolkit().getImage(metapod.class.getResource(path));
	}
}
