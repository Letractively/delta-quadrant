package com.k42b3.metapod.terran;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.TerranObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.terran.items.Gebaeudepanzerung;
import com.k42b3.metapod.terran.items.HochsicherheitsVerfolgung;
import com.k42b3.metapod.terran.items.Level1UpgradeInfanterieAttack;
import com.k42b3.metapod.terran.items.Level1UpgradeInfanterieDefense;
import com.k42b3.metapod.terran.items.Neostahlrahmen;

public class EngineeringBay extends TerranObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Level1UpgradeInfanterieAttack());
		items.put(1, new Level1UpgradeInfanterieDefense());
		items.put(5, new HochsicherheitsVerfolgung());
		items.put(6, new Neostahlrahmen());
		items.put(7, new Gebaeudepanzerung());
		items.put(14, new Cancel());

		return items;
	}
}
