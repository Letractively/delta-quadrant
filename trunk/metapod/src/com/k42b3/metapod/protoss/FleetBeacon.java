package com.k42b3.metapod.protoss;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.ProtossObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.protoss.items.GravitationKatapult;
import com.k42b3.metapod.protoss.items.LuftstromRuder;

public class FleetBeacon extends ProtossObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new LuftstromRuder());
		items.put(1, new GravitationKatapult());
		items.put(14, new Cancel());

		return items;
	}
}
