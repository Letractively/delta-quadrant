package com.k42b3.espeon.model;

import java.io.File;
import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.k42b3.espeon.espeon;

public class template implements TableModel
{
	private ArrayList<TableModelListener> listener;
	
	private String[] columns = {"Active", "Template"};
	private Object[][] rows;

	public template()
	{
		this.listener = new ArrayList<TableModelListener>();

		File dir = new File(espeon.path);

		if(dir.isDirectory())
		{
			File[] templates = dir.listFiles();

			int length = 0;

			for(int i = 0; i < templates.length; i++)
			{
				if(templates[i].isFile() && !templates[i].isHidden())
				{
					length++;
				}
			}


			this.rows = new Object[length][2];
			int j = 0;
			
			for(int i = 0; i < templates.length; i++)
			{
				if(templates[i].isFile() && !templates[i].isHidden())
				{
					this.rows[j][0] = Boolean.TRUE;
					this.rows[j][1] = templates[i].getName();
					
					j++;
				}
			}
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
