package com.ModDamage.Alias;

import java.util.Collection;

import com.ModDamage.Alias.Aliaser.CollectionAliaser;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Matchables.EntityType;

public class TypeAliaser extends CollectionAliaser<EntityType> 
{
	public static TypeAliaser aliaser = new TypeAliaser();
	public static Collection<EntityType> match(ScriptLine scriptLine) { return aliaser.matchAlias(scriptLine); }
	public static Collection<EntityType> match(ScriptLine scriptLine, String string) { return aliaser.matchAlias(scriptLine, string); }
	
	public TypeAliaser() {super(AliasManager.Type.name()); }

	@Override
	protected EntityType matchNonAlias(String key){ return EntityType.getElementNamed(key); }

	//@Override
	//protected String getObjectName(ModDamageElement object){ return object.name(); }
}