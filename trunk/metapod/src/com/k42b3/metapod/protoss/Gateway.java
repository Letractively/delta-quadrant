package com.k42b3.metapod.protoss;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.ProtossObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.protoss.items.DarkTemplar;
import com.k42b3.metapod.protoss.items.HighTemplar;
import com.k42b3.metapod.protoss.items.Sentry;
import com.k42b3.metapod.protoss.items.Stalker;
import com.k42b3.metapod.protoss.items.Zealot;

public class Gateway extends ProtossObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Zealot());
		items.put(1, new Sentry());
		items.put(2, new Stalker());
		items.put(5, new HighTemplar());
		items.put(6, new DarkTemplar());
		items.put(14, new Cancel());

		return items;
	}
}
