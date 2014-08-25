package com.moddamage.alias;

import com.moddamage.alias.Aliaser.CollectionAliaser;
import com.moddamage.backend.ArmorSet;

import java.util.Collection;

public class ArmorAliaser extends CollectionAliaser<ArmorSet> 
{
	public static ArmorAliaser aliaser = new ArmorAliaser();
	public static Collection<ArmorSet> match(String string) { return aliaser.matchAlias(string); }
	
	public ArmorAliaser(){ super(AliasManager.Armor.name()); }
	@Override
	protected ArmorSet matchNonAlias(String key){ return ArmorSet.getNew(key); }
	//@Override
	//protected String getObjectName(ArmorSet object){ return object.toString(); }
}
