package com.k42b3.metapod.terran;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.TerranObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.terran.items.Hellion;
import com.k42b3.metapod.terran.items.Reaktor;
import com.k42b3.metapod.terran.items.SiegeTank;
import com.k42b3.metapod.terran.items.Techlabor;
import com.k42b3.metapod.terran.items.Thor;

public class Factory extends TerranObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Hellion());
		items.put(1, new SiegeTank());
		items.put(2, new Thor());
		items.put(10, new Techlabor());
		items.put(11, new Reaktor());
		items.put(14, new Cancel());

		return items;
	}
}
