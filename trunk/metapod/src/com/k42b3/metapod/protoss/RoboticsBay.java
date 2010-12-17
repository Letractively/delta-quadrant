package com.k42b3.metapod.protoss;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.ProtossObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.protoss.items.Gravitationsantrieb;
import com.k42b3.metapod.protoss.items.Gravitationsbooster;
import com.k42b3.metapod.protoss.items.Thermolanze;

public class RoboticsBay extends ProtossObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Gravitationsbooster());
		items.put(1, new Gravitationsantrieb());
		items.put(2, new Thermolanze());
		items.put(14, new Cancel());

		return items;
	}
}
