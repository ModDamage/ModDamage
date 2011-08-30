package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;

public class ArmorAliaser extends Aliaser<ArmorSet> 
{
	private static final long serialVersionUID = 1304321966061887438L;

	public ArmorAliaser(){ super("Armor");}

	@Override
	protected ArmorSet matchNonAlias(String key)
	{ 
		ArmorSet armorSet = new ArmorSet(key);
		return (armorSet.isValid()?armorSet:null);
	}

	@Override
	protected String getObjectName(ArmorSet object){ return object.toString();}
}
