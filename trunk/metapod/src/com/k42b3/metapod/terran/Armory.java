package com.k42b3.metapod.terran;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.TerranObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.terran.items.Level1UpgradFahrzeugAttack;
import com.k42b3.metapod.terran.items.Level1UpgradFahrzeugDefense;
import com.k42b3.metapod.terran.items.Level1UpgradSchiffAttack;
import com.k42b3.metapod.terran.items.Level1UpgradSchiffDefense;

public class Armory extends TerranObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Level1UpgradFahrzeugAttack());
		items.put(1, new Level1UpgradFahrzeugDefense());
		items.put(5, new Level1UpgradSchiffAttack());
		items.put(6, new Level1UpgradSchiffDefense());
		items.put(14, new Cancel());

		return items;
	}
}
