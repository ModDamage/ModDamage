package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class ArmorAliaser extends CollectionAliaser<ArmorSet> 
{
	public ArmorAliaser(){ super(AliasManager.Armor.name());}
	@Override
	protected ArmorSet matchNonAlias(String key){ return ArmorSet.getNew(key);}
	@Override
	protected String getObjectName(ArmorSet object){ return object.toString();}
}
