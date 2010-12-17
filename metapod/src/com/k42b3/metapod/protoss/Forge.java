package com.k42b3.metapod.protoss;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.ProtossObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.protoss.items.Level1UpgradeGroundDefense;
import com.k42b3.metapod.protoss.items.Level1UpgradeGroundWeapon;
import com.k42b3.metapod.protoss.items.Level1UpgradeShield;

public class Forge extends ProtossObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Level1UpgradeGroundWeapon());
		items.put(1, new Level1UpgradeGroundDefense());
		items.put(2, new Level1UpgradeShield());
		items.put(14, new Cancel());

		return items;
	}
}
