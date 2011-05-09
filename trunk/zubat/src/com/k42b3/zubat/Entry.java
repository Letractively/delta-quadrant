package com.k42b3.zubat;

import javax.swing.UIManager;

public class Entry 
{
	public static void main(String[] args)
	{
        try
        {
    		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

        	UIManager.setLookAndFeel(lookAndFeel);


         	new Zubat();
        }
        catch(Exception e)
        {
        	System.err.print(e.getMessage());
        }
	}
}
