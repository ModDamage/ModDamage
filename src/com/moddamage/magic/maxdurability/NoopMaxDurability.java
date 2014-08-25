package com.ModDamage.Magic.MaxDurability;

import org.bukkit.inventory.ItemStack;

public class NoopMaxDurability implements IMagicMaxDurability
{
	@Override
	public int getMaxDurability(ItemStack itemStack)
	{
		return 0;
	}

}
