package com.k42b3.metapod.terran;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.TerranObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.terran.items.Banshee;
import com.k42b3.metapod.terran.items.Battlecruiser;
import com.k42b3.metapod.terran.items.Medivac;
import com.k42b3.metapod.terran.items.Raven;
import com.k42b3.metapod.terran.items.Reaktor;
import com.k42b3.metapod.terran.items.Techlabor;
import com.k42b3.metapod.terran.items.Viking;

public class Starport extends TerranObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Viking());
		items.put(1, new Medivac());
		items.put(2, new Raven());
		items.put(3, new Banshee());
		items.put(4, new Battlecruiser());
		items.put(10, new Techlabor());
		items.put(11, new Reaktor());
		items.put(14, new Cancel());

		return items;
	}
}
