package com.k42b3.zubat;

import com.k42b3.zubat.form.Input;

public class ReferenceItem 
{
	private String valueField;
	private String labelField;
	private String src;
	private Input input;
	private SearchPanel panel;

	public ReferenceItem(String valueField, String labelField, String src, Input input)
	{
		this.setValueField(valueField);
		this.setLabelField(labelField);
		this.setSrc(src);
		this.setInput(input);
	}

	public String getValueField() 
	{
		return valueField;
	}

	public void setValueField(String valueField) 
	{
		this.valueField = valueField;
	}

	public String getLabelField() 
	{
		return labelField;
	}

	public void setLabelField(String labelField) 
	{
		this.labelField = labelField;
	}

	public String getSrc() 
	{
		return src;
	}

	public void setSrc(String src) 
	{
		this.src = src;
	}

	public Input getInput() 
	{
		return input;
	}

	public void setInput(Input input) 
	{
		this.input = input;
	}

	public SearchPanel getPanel() 
	{
		return panel;
	}

	public void setPanel(SearchPanel panel) 
	{
		this.panel = panel;
	}
}
