package com.k42b3.zubat.form;

import javax.swing.JTextArea;

public class Textarea extends JTextArea implements FormElementInterface
{
	public String getValue() 
	{
		return this.getText();
	}
}
