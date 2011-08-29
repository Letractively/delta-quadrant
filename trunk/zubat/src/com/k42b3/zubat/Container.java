package com.k42b3.zubat;

import java.awt.Component;
import java.util.ArrayList;

public interface Container 
{
	public Component getComponent();
	public void onLoad(Http http, ServiceItem item, ArrayList<String> fields);
}
