/**
 * zubat
 * 
 * An java application to access the API of amun. It is used to debug and
 * control a website based on amun. This is the reference implementation 
 * howto access the api. So feel free to hack and extend.
 * 
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of oat. oat is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * oat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with oat. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.zubat;

import java.util.ArrayList;
import java.util.Stack;

import javax.swing.table.AbstractTableModel;

/**
 * TrafficTableModel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant
 * @version    $Revision$
 */
public class TrafficTableModel extends AbstractTableModel
{
	private ArrayList<String> fields = new ArrayList<String>();
	private ArrayList<TrafficItem> rows = new ArrayList<TrafficItem>();

	public TrafficTableModel()
	{
		fields.add("Method");
		fields.add("Response");
		fields.add("Url");
	}

	public TrafficItem getRow(int rowIndex)
	{
		if(rowIndex >= 0 && rowIndex < rows.size())
		{
			return rows.get(rowIndex);
		}

		return null;
	}

	public void addTraffic(TrafficItem item)
	{
		rows.add(item);

		this.fireTableDataChanged();
	}

	public int getColumnCount()
	{
		return fields.size();
	}

	public String getColumnName(int columnIndex)
	{
		return fields.get(columnIndex);
	}

	public int getRowCount() 
	{
		return rows.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(rowIndex >= 0 && rowIndex < rows.size())
		{
			switch(columnIndex)
			{
				case 0: return rows.get(rowIndex).getMethod();
				case 1: return rows.get(rowIndex).getResponseCode();
				case 2: return rows.get(rowIndex).getUrl();
			}
		}

		return null;
	}
}
