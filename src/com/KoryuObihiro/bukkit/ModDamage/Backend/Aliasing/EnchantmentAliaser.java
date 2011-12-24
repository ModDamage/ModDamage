package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import org.bukkit.enchantments.Enchantment;

import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class EnchantmentAliaser extends CollectionAliaser<Enchantment> 
{
	public EnchantmentAliaser(){ super(AliasManager.Enchantment.name());}

	@Override
	protected Enchantment matchNonAlias(String key)
	{
		for(Enchantment enchantment : Enchantment.values())
			if(key.equalsIgnoreCase(enchantment.getName()))
				return enchantment;
		return null;
	}

	@Override
	protected String getObjectName(Enchantment object){ return object.getName();}
}
