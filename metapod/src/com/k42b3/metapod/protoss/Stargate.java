package com.k42b3.metapod.protoss;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.ProtossObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.protoss.items.Carrier;
import com.k42b3.metapod.protoss.items.Phoenix;
import com.k42b3.metapod.protoss.items.VoidRay;

public class Stargate extends ProtossObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Phoenix());
		items.put(1, new VoidRay());
		items.put(2, new Carrier());
		items.put(14, new Cancel());

		return items;
	}
}
