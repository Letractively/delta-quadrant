package com.k42b3.metapod.zerg;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.ZergObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.zerg.items.Level1UpgradeDefense;
import com.k42b3.metapod.zerg.items.Level1UpgradeMeleeAttack;
import com.k42b3.metapod.zerg.items.Level1UpgradeRangeAttack;

public class EvolutionChamber extends ZergObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Level1UpgradeMeleeAttack());
		items.put(1, new Level1UpgradeRangeAttack());
		items.put(2, new Level1UpgradeDefense());
		items.put(14, new Cancel());

		return items;
	}
}
