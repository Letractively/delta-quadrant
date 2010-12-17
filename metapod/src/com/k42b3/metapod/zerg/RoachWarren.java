package com.k42b3.metapod.zerg;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.ZergObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.zerg.items.GilaNuebildung;
import com.k42b3.metapod.zerg.items.Graeberklauen;

public class RoachWarren extends ZergObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new GilaNuebildung());
		items.put(1, new Graeberklauen());
		items.put(14, new Cancel());

		return items;
	}
}
