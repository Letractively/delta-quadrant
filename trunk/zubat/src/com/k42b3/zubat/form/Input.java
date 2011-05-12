package com.k42b3.zubat.form;

import javax.swing.JTextField;

public class Input extends JTextField implements FormElementInterface
{
	public String getValue() 
	{
		return this.getText();
	}
}
