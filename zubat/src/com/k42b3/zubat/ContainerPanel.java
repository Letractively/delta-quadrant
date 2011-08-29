package com.k42b3.zubat;

import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.JPanel;

public class ContainerPanel extends JPanel
{
	private CardLayout cl;

	public ContainerPanel()
	{
		super();

		cl = new CardLayout();

		this.setLayout(cl);
	}
}
