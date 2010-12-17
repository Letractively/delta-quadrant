package com.k42b3.metapod.terran;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.TerranObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.terran.items.Cloak;
import com.k42b3.metapod.terran.items.MoebiusReaktor;
import com.k42b3.metapod.terran.items.NuclearMissle;

public class GhostAcademy extends TerranObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Cloak());
		items.put(1, new MoebiusReaktor());
		items.put(10, new NuclearMissle());
		items.put(14, new Cancel());

		return items;
	}
}
