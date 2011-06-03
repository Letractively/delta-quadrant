package com.k42b3.zubat;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.k42b3.zubat.form.FormElementInterface;

public class CheckboxList extends JPanel implements FormElementInterface
{
	private String url;
	private Http http;

	private CheckboxListTableModel tm;
	private JTable table;

	public CheckboxList(String url, Http http)
	{
		this.setLayout(new BorderLayout());

		this.url = url;
		this.http = http;

		try
		{
			tm = new CheckboxListTableModel(url, http);

			table = new JTable(tm);

			table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

			table.getColumnModel().getColumn(0).setMinWidth(60);
			table.getColumnModel().getColumn(0).setMaxWidth(60);	
			
			this.add(new JScrollPane(table), BorderLayout.CENTER);
		}
		catch(Exception e)
		{
		}
	}

	public String getValue() 
	{
		StringBuilder values = new StringBuilder();

		for(int i = 0; i < tm.getRowCount(); i++)
		{
			boolean selected = (Boolean) tm.getValueAt(i, 0);

			if(selected)
			{
				values.append(tm.getValueAt(i, 2).toString());

				if(i < tm.getRowCount() - 2)
				{
					values.append(",");
				}
			}
		}

		return values.toString();
	}
}
