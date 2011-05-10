package com.k42b3.zubat;

import java.util.ArrayList;

public class FormItem 
{
	private String classType;
	private String ref;
	private String label;
	private String type;
	private String value;
	private String method;
	private ArrayList<FormItem> items = new ArrayList<FormItem>();

	public String getClassType() 
	{
		return classType;
	}

	public void setClassType(String classType) 
	{
		this.classType = classType;
	}

	public String getRef() 
	{
		return ref;
	}

	public void setRef(String ref) 
	{
		this.ref = ref;
	}

	public String getLabel() 
	{
		return label;
	}

	public void setLabel(String label) 
	{
		this.label = label;
	}

	public String getType() 
	{
		return type;
	}

	public void setType(String type) 
	{
		this.type = type;
	}

	public String getValue() 
	{
		return value;
	}

	public void setValue(String value) 
	{
		this.value = value;
	}

	public String getMethod() 
	{
		return method;
	}

	public void setMethod(String method) 
	{
		this.method = method;
	}
	
	public ArrayList<FormItem> getItems() 
	{
		return items;
	}

	public void setItems(ArrayList<FormItem> items) 
	{
		this.items = items;
	}
}
