package com.ModDamage.Backend.Aliasing;

import java.util.Collection;

import com.ModDamage.Backend.ArmorSet;
import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class ArmorAliaser extends CollectionAliaser<ArmorSet> 
{
	static ArmorAliaser aliaser = new ArmorAliaser();
	public static Collection<ArmorSet> match(String string) { return aliaser.matchAlias(string); }
	
	public ArmorAliaser(){ super(AliasManager.Armor.name());}
	@Override
	protected ArmorSet matchNonAlias(String key){ return ArmorSet.getNew(key);}
	@Override
	protected String getObjectName(ArmorSet object){ return object.toString();}
}
