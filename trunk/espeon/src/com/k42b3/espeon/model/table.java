/**
 * espeon
 * 
 * With espeon you can generate sourcecode from database structures. It was 
 * mainly developed to generate PHP classes for the psx framework (phpsx.org) 
 * but because it uses a template engine (FreeMarker) you can use it for any 
 * purpose you like.
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

package com.k42b3.espeon.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * table
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision: 31 $
 */
public class table implements TableModel
{
	private ArrayList<TableModelListener> listener;
	
	private String[] columns = {"Active", "Field", "Type", "Null", "Key", "Default", "Extra"};
	private Object[][] rows;

	public table()
	{
		this.listener = new ArrayList<TableModelListener>();

		this.rows = new Object[0][0];
	}
	
	public void load_table(Connection con, String table)
	{
		try
		{
			// get table structure
			PreparedStatement ps = con.prepareStatement("DESCRIBE " + table);

			ps.execute();

			ResultSet result = ps.getResultSet();

			result.last();

			this.rows = new Object[result.getRow()][this.columns.length];

			result.beforeFirst();

			while(result.next())
			{
				int row = result.getRow() - 1;

				this.rows[row][0] = Boolean.TRUE;
				this.rows[row][1] = result.getString("Field");
				this.rows[row][2] = result.getString("Type");
				this.rows[row][3] = result.getString("Null");
				this.rows[row][4] = result.getString("Key");
				this.rows[row][5] = result.getString("Default");
				this.rows[row][6] = result.getString("Extra");
			}


			// get table data
			/*
			ps = con.prepareStatement("SELECT * FROM " + table);

			ps.execute();

			result = ps.getResultSet();

			if(result.first())
			{
				result.last();

				this.rows = new Object[result.getRow()][this.columns.length];

				result.beforeFirst();

				while(result.next())
				{
					this.rows[result.getRow() - 1][0] = Boolean.TRUE;

					for(int i = 1; i < this.columns.length; i++)
					{
						this.rows[result.getRow() - 1][i] = result.getObject(i);
					}
				}
			}
			*/


			// update table
			for(int i = 0; i < this.listener.size(); i++)
			{
				TableModelEvent e = new TableModelEvent(this, TableModelEvent.HEADER_ROW);
				
				this.listener.get(i).tableChanged(e);
			}
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "SQL Exception", JOptionPane.WARNING_MESSAGE);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void addTableModelListener(TableModelListener l) 
	{
		if(!this.listener.contains(l))
		{
			this.listener.add(l);
		}
	}

	public Class<?> getColumnClass(int columnIndex) 
	{
		if(columnIndex == 0)
		{
			return Boolean.class;
		}
		else
		{
			return String.class;
		}
	}

	public int getColumnCount() 
	{
		return this.columns.length;
	}

	public String getColumnName(int columnIndex) 
	{
		if(columnIndex >= 0 && columnIndex < this.columns.length)
		{
			return this.columns[columnIndex];
		}
		else
		{
			return null;
		}
	}

	public int getRowCount() 
	{
		return this.rows.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		if((rowIndex >= 0 && rowIndex < this.rows.length) && (columnIndex >= 0 && columnIndex < this.columns.length))
		{
			return this.rows[rowIndex][columnIndex];
		}
		else
		{
			return null;
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columnIndex == 0 ? true : false;
	}

	public void removeTableModelListener(TableModelListener arg0) 
	{
		this.listener.remove(arg0);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
	{
		if((rowIndex >= 0 && rowIndex < this.rows.length) && (columnIndex >= 0 && columnIndex < this.columns.length))
		{
			this.rows[rowIndex][columnIndex] = aValue;
		}
	}
}
