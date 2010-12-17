package com.k42b3.metapod.protoss;

import java.util.HashMap;

import com.k42b3.metapod.AbstractItem;
import com.k42b3.metapod.ProtossObject;
import com.k42b3.metapod.neutral.items.Cancel;
import com.k42b3.metapod.protoss.items.Illusion;
import com.k42b3.metapod.protoss.items.Level1UpgradeAirShield;
import com.k42b3.metapod.protoss.items.Level1UpgradeAirWeapon;
import com.k42b3.metapod.protoss.items.WarpgateTechnologie;

public class CyberneticsCore extends ProtossObject
{
	public HashMap<Integer, AbstractItem> getItems() 
	{
		HashMap<Integer, AbstractItem> items = new HashMap<Integer, AbstractItem>();

		items.put(0, new Level1UpgradeAirWeapon());
		items.put(1, new Level1UpgradeAirShield());
		items.put(5, new Illusion());
		items.put(10, new WarpgateTechnologie());
		items.put(14, new Cancel());

		return items;
	}
}
