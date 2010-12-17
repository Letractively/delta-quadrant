package com.k42b3.metapod.zerg;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.ZergObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.zerg.items.Level1UpgradeAirAttack;
import com.k42b3.metapod.zerg.items.Level1UpgradeAirDefense;
import com.k42b3.metapod.zerg.items.UpgradeSpire;

public class Spire extends ZergObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Level1UpgradeAirAttack());
		items.put(1, new Level1UpgradeAirDefense());
		items.put(10, new UpgradeSpire());
		items.put(14, new Cancel());

		return items;
	}
}
