package com.ModDamage.Backend.Aliasing;

import java.util.Collection;

import org.bukkit.enchantments.Enchantment;

import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class EnchantmentAliaser extends CollectionAliaser<Enchantment> 
{
	public static EnchantmentAliaser aliaser = new EnchantmentAliaser();
	public static Collection<Enchantment> match(String string) { return aliaser.matchAlias(string); }
	
	public EnchantmentAliaser(){ super(AliasManager.Enchantment.name()); }

	@Override
	protected Enchantment matchNonAlias(String key)
	{
		for(Enchantment enchantment : Enchantment.values())
			if(key.equalsIgnoreCase(enchantment.getName()))
				return enchantment;
		return null;
	}

	//@Override
	//protected String getObjectName(Enchantment object){ return object.getName(); }
}
