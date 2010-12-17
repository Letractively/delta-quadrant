package com.k42b3.metapod.terran;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.TerranObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.terran.items.BehemothReaktor;
import com.k42b3.metapod.terran.items.Neuausruestung;

public class FusionCore extends TerranObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Neuausruestung());
		items.put(1, new BehemothReaktor());
		items.put(14, new Cancel());

		return items;
	}
}
