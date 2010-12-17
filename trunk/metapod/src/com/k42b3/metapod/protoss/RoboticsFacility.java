package com.k42b3.metapod.protoss;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.ProtossObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.protoss.items.Colossus;
import com.k42b3.metapod.protoss.items.Immortal;
import com.k42b3.metapod.protoss.items.Observer;
import com.k42b3.metapod.protoss.items.WarpPrism;

public class RoboticsFacility extends ProtossObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Observer());
		items.put(1, new WarpPrism());
		items.put(2, new Immortal());
		items.put(2, new Colossus());
		items.put(14, new Cancel());

		return items;
	}
}
