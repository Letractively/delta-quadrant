package com.k42b3.metapod.zerg;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.ZergObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.zerg.items.Larva;
import com.k42b3.metapod.zerg.items.Queen;
import com.k42b3.metapod.zerg.items.UpgradeLair;

public class Hatchery extends ZergObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Larva());
		items.put(1, new Queen());
		items.put(10, new UpgradeLair());
		items.put(14, new Cancel());

		return items;
	}
}
