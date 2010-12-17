package com.k42b3.metapod.terran;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.TerranObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.terran.items.OrbitalCommand;
import com.k42b3.metapod.terran.items.PlaneterFortress;
import com.k42b3.metapod.terran.items.SCV;

public class CommandCenter extends TerranObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new SCV());
		items.put(3, new OrbitalCommand());
		items.put(4, new PlaneterFortress());
		items.put(14, new Cancel());

		return items;
	}
}
