package com.k42b3.zubat.form;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

public class Select extends JComboBox implements FormElementInterface
{
	public Select(ComboBoxModel model)
	{
		super(model);
	}

	public String getValue() 
	{
		Object item = this.getSelectedItem();
		
		if(item instanceof SelectItem)
		{
			return ((SelectItem) item).getKey();
		}
		else
		{
			return null;
		}
	}
}
