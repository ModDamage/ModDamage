package com.ModDamage.Backend;

import java.util.Map;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentsRef
{
	public Map<Enchantment, Integer> map;
	
	public EnchantmentsRef(Map<Enchantment, Integer> map)
	{
		this.map = map;
	}
}
