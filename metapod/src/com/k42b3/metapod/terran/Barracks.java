package com.k42b3.metapod.terran;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.TerranObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.terran.items.Ghost;
import com.k42b3.metapod.terran.items.Marauder;
import com.k42b3.metapod.terran.items.Marine;
import com.k42b3.metapod.terran.items.Reaktor;
import com.k42b3.metapod.terran.items.Reaper;
import com.k42b3.metapod.terran.items.Techlabor;

public class Barracks extends TerranObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Marine());
		items.put(1, new Marauder());
		items.put(2, new Reaper());
		items.put(3, new Ghost());
		items.put(10, new Techlabor());
		items.put(11, new Reaktor());
		items.put(14, new Cancel());

		return items;
	}
}
