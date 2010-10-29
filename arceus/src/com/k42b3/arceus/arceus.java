/**
 * arceus
 * 
 * arceus is a little tool to train converting numbers into different numeric 
 * systems
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

package com.k42b3.arceus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * arceus
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class arceus extends JFrame
{
	public static String ver = "0.0.1 beta";
	
	private static final int HEX = 0;
	private static final int DEZ = 1;
	private static final int BIN = 2;
	
	private JComboBox cbo_from_type;
	private JTextField txt_from_number;
	private JComboBox cbo_to_type;
	private JTextField txt_to_number;
	private JLabel lbl_solution;
	private JComboBox cbo_range;

	public arceus()
	{
		this.setTitle("arceus (version: " + ver + ")");

		this.setLocation(100, 100);

		this.setSize(450, 400);

		this.setMinimumSize(this.getSize());

		this.setResizable(false);

		this.setLayout(new BorderLayout());


		//
		String[] numeric_type = {"Hex (Base: 16)", "Dezimal (Base: 10)", "Binär (Base: 2)"};


		JPanel convert_panel = new JPanel();

		convert_panel.setLayout(new GridLayout(0, 1));


		JPanel from_panel = new JPanel();

		FlowLayout from_layout = new FlowLayout();

		from_layout.setAlignment(FlowLayout.LEFT);

		from_panel.setLayout(from_layout);

		this.cbo_from_type = new JComboBox(numeric_type);

		this.cbo_from_type.setSelectedIndex(arceus.BIN);

		cbo_from_type.setPreferredSize(new Dimension(120, 24));

		from_panel.add(cbo_from_type);

		this.txt_from_number = new JTextField();

		txt_from_number.setPreferredSize(new Dimension(200, 24));

		from_panel.add(txt_from_number);

		JButton from_generate = new JButton("Generate");
		
		from_generate.addActionListener(new generate_handler());

		from_generate.setPreferredSize(new Dimension(100, 24));

		from_panel.add(from_generate);


		convert_panel.add(from_panel);


		JPanel to_panel = new JPanel();

		FlowLayout to_layout = new FlowLayout();

		to_layout.setAlignment(FlowLayout.LEFT);

		to_panel.setLayout(to_layout);

		this.cbo_to_type = new JComboBox(numeric_type);

		this.cbo_to_type.setSelectedIndex(arceus.DEZ);

		cbo_to_type.setPreferredSize(new Dimension(120, 24));

		to_panel.add(cbo_to_type);

		this.txt_to_number = new JTextField();

		txt_to_number.setPreferredSize(new Dimension(200, 24));

		to_panel.add(txt_to_number);

		JButton to_solve = new JButton("Solve");

		to_solve.addActionListener(new solve_handler());

		to_solve.setPreferredSize(new Dimension(100, 24));

		to_panel.add(to_solve);


		convert_panel.add(to_panel);


		this.add(convert_panel, BorderLayout.NORTH);


		this.lbl_solution = new JLabel("-");

		lbl_solution.setFont(new Font("Courier", Font.PLAIN, 14));
		lbl_solution.setVerticalAlignment(JLabel.TOP);

		JScrollPane scp_solution = new JScrollPane(this.lbl_solution);
		scp_solution.setBorder(new EmptyBorder(5, 5, 5, 5));

		this.add(scp_solution, BorderLayout.CENTER);


		JPanel options_panel = new JPanel();
		
		FlowLayout options_layout = new FlowLayout();

		options_layout.setAlignment(FlowLayout.RIGHT);

		options_panel.setLayout(options_layout);
		
		JLabel lbl_range = new JLabel("Max generate value:");
		
		options_panel.add(lbl_range);
		
		String[] ranges = {"8", "16", "32", "64", "128", "256", "512", "1024", "2048", "4096"};

		this.cbo_range = new JComboBox(ranges);
		this.cbo_range.setPreferredSize(new Dimension(100, 24));
		this.cbo_range.setSelectedIndex(4);
		
		options_panel.add(this.cbo_range);


		JButton btn_about = new JButton("About");
		btn_about.setPreferredSize(new Dimension(100, 24));
		btn_about.addActionListener(new about_handler());
		
		options_panel.add(btn_about);
		
		JButton btn_exit = new JButton("Exit");
		btn_exit.setPreferredSize(new Dimension(100, 24));
		btn_exit.addActionListener(new exit_handler());
		
		options_panel.add(btn_exit);
		
		this.add(options_panel, BorderLayout.SOUTH);
		
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private String[] get_signs_of_type(int type)
	{
		switch(type)
		{
			case arceus.HEX:

				String[] hex_signs = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

				return hex_signs;

			case arceus.DEZ:

				String[] dez_signs = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

				return dez_signs;

			default:
			case arceus.BIN:

				String[] bin_signs = {"0", "1"};

				return bin_signs;
		}
	}

	private int get_sign_pos(String[] signs, char sign)
	{
		for(int i = 0; i < signs.length; i++)
		{
			if(signs[i].charAt(0) == sign)
			{
				return i;
			}
		}
		
		return -1;
	}

	private String str_left_pad(String str, char sign, int size)
	{
		if(str.length() < size)
		{
			while(str.length() < size)
			{
				str = sign + str;
			}
			
			return str;
		}
		else
		{
			return str;
		}
	}
	
	public class generate_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			lbl_solution.setText("");
			
			txt_to_number.setText("");
			
			Random randomGenerator = new Random();

			int max_int = Integer.parseInt(cbo_range.getSelectedItem().toString());			
			int rnd_number = randomGenerator.nextInt(max_int);

			switch(cbo_from_type.getSelectedIndex())
			{
				case arceus.HEX:

					txt_from_number.setText(Integer.toHexString(rnd_number).toUpperCase());

					break;

				case arceus.DEZ:

					txt_from_number.setText("" + rnd_number);

					break;

				case arceus.BIN:

					txt_from_number.setText(Integer.toBinaryString(rnd_number));

					break;
			}
		}
	}
	
	public class solve_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			lbl_solution.setText("");

			txt_from_number.setText(txt_from_number.getText().toUpperCase());

			String[] from_signs = get_signs_of_type(cbo_from_type.getSelectedIndex());
			String[] to_signs = get_signs_of_type(cbo_to_type.getSelectedIndex());


			StringBuilder solution = new StringBuilder();
			solution.append("<html>");

			if(cbo_to_type.getSelectedIndex() == arceus.DEZ)
			{
				int base = from_signs.length;
				double result = 0;
				
				String number = new StringBuffer(txt_from_number.getText()).reverse().toString();

				for(int i = 0; i < number.length(); i++)
				{
					char n = number.charAt(i);
					int p = get_sign_pos(from_signs, n);

					if(p == -1)
					{
						solution.append("<b><font color=\"red\">Invalid sign in number</font></b>");

						break;
					}
					else
					{
						result = result + (p * Math.pow(base, i));

						if(p == 0)
						{
							solution.append(n + " " + " * " + base + " ^ " + i + " = <s>" + (int) Math.pow(base, i) + "</s><br />");
						}
						else
						{
							solution.append(n + " " + " * " + base + " ^ " + i + " = " + (int) Math.pow(base, i) + "<br />");
						}
					}
				}
				
				solution.append("<hr />");
				solution.append("<b>" + (int) result + "<sub>" + to_signs.length + "</sub></b>");
				
				txt_to_number.setText("" + (int) result);
			}
			else if(cbo_from_type.getSelectedIndex() == arceus.DEZ)
			{
				int base = to_signs.length;
				String result = "";
				
				int number = Integer.parseInt(txt_from_number.getText());
				int rest;

				while(number > 0)
				{
					solution.append(number + " / " + base + " = " + (number / base) + " | " + to_signs[(number % base)] + "<br />");

					rest = number % base;
					number = number / base;

					result+= to_signs[rest];
				}

				result = new StringBuffer(result).reverse().toString();
				
				solution.append("<hr />");
				solution.append("<b>" + result + "<sub>" + base + "</sub></b>");
				
				txt_to_number.setText(result);
			}
			else if(cbo_from_type.getSelectedIndex() == arceus.BIN && cbo_to_type.getSelectedIndex() == arceus.HEX)
			{
				String result = "";
				String number = txt_from_number.getText();
				
				
				// pad 0
				while(number.length() % 4 != 0)
				{
					number = "0" + number;
				}
				
				// divide blocks
				String[] blocks = new String[number.length() / 4];

				StringBuilder blocks_format = new StringBuilder();
				
				for(int i = 0; i < number.length(); i++)
				{
					if(i % 4 == 0)
					{
						blocks[i / 4] = number.substring(i, i + 4);

						blocks_format.append(" ");
					}
					
					blocks_format.append(number.charAt(i));
				}

				solution.append(blocks_format + "<br />");

				for(int i = 0; i < blocks.length; i++)
				{
					int dez = Integer.parseInt(blocks[i], 2);
					String hex = Integer.toHexString(dez);

					solution.append("&nbsp;&nbsp;&nbsp;" + hex.toUpperCase() + " ");

					result+= hex;
				}

				txt_to_number.setText(result);
			}
			else if(cbo_from_type.getSelectedIndex() == arceus.HEX && cbo_to_type.getSelectedIndex() == arceus.BIN)
			{
				String result = "";
				String number = txt_from_number.getText();
				
				// divide blocks
				String[] blocks = new String[number.length()];

				StringBuilder blocks_format = new StringBuilder();
				
				for(int i = 0; i < number.length(); i++)
				{
					blocks[i] = "" + number.charAt(i);

					blocks_format.append("&nbsp;&nbsp;&nbsp;" + number.charAt(i) + " ");
				}
				
				solution.append(blocks_format + "<br />");

				for(int i = 0; i < blocks.length; i++)
				{
					int dez = Integer.parseInt(blocks[i], 16);
					String bin = str_left_pad(Integer.toBinaryString(dez), '0', 4);

					solution.append(bin + " ");

					result+= bin;
				}

				txt_to_number.setText(result);
			}

			solution.append("</html>");
			
			lbl_solution.setText(solution.toString());
		}
	}

	public class about_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			StringBuilder out = new StringBuilder();
			
			out.append("arceus version " + ver + "\n");
			out.append("author: Christoph \"k42b3\" Kappestein" + "\n");
			out.append("website: http://code.google.com/p/delta-quadrant" + "\n");
			out.append("license: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
			out.append("\n");
			out.append("arceus is a little tool to train converting numbers into different numeric systems" + "\n");
			
			JOptionPane.showMessageDialog(null, out, "About", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public class exit_handler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.exit(0);
		}
	}
}
