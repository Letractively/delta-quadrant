package com.k42b3.metapod.protoss;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.ProtossObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.protoss.items.Mothership;
import com.k42b3.metapod.protoss.items.Probe;
import com.k42b3.metapod.protoss.items.Zeitschleife;

public class Nexus extends ProtossObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Probe());
		items.put(1, new Mothership());
		items.put(10, new Zeitschleife());
		items.put(14, new Cancel());

		return items;
	}
}
