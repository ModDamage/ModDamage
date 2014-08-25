package com.ModDamage.Alias;

import java.util.Collection;

import com.ModDamage.Alias.Aliaser.CollectionAliaser;
import com.ModDamage.Backend.ArmorSet;
import com.ModDamage.Backend.ScriptLine;

public class ArmorAliaser extends CollectionAliaser<ArmorSet> 
{
	public static ArmorAliaser aliaser = new ArmorAliaser();
	public static Collection<ArmorSet> match(ScriptLine scriptLine, String string) { return aliaser.matchAlias(scriptLine, string); }
	public static Collection<ArmorSet> match(ScriptLine scriptLine) { return aliaser.matchAlias(scriptLine); }
	
	public ArmorAliaser(){ super(AliasManager.Armor.name()); }
	@Override
	protected ArmorSet matchNonAlias(String key){ return ArmorSet.getNew(key); }
	//@Override
	//protected String getObjectName(ArmorSet object){ return object.toString(); }
}
