package com.k42b3.metapod.zerg;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.ZergObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.zerg.items.Bauchbeutel;
import com.k42b3.metapod.zerg.items.Larva;
import com.k42b3.metapod.zerg.items.PneumatischerPanzer;
import com.k42b3.metapod.zerg.items.Queen;

public class Hive extends ZergObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Larva());
		items.put(1, new Queen());
		items.put(5, new PneumatischerPanzer());
		items.put(6, new Bauchbeutel());
		items.put(14, new Cancel());

		return items;
	}
}
