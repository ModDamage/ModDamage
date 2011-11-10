package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class ArmorAliaser extends CollectionAliaser<ArmorSet> 
{
	private static final long serialVersionUID = 1304321966061887438L;

	public ArmorAliaser(){ super("Armor");}

	@Override
	protected ArmorSet matchNonAlias(String key){ return ArmorSet.getNew(key);}

	@Override
	protected String getObjectName(ArmorSet object){ return object.toString();}
}
