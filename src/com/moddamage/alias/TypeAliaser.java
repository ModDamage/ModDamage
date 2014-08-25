package com.moddamage.alias;

import com.moddamage.alias.Aliaser.CollectionAliaser;
import com.moddamage.matchables.EntityType;

import java.util.Collection;

public class TypeAliaser extends CollectionAliaser<EntityType> 
{
	public static TypeAliaser aliaser = new TypeAliaser();
	public static Collection<EntityType> match(String string) { return aliaser.matchAlias(string); }
	
	public TypeAliaser() {super(AliasManager.Type.name()); }

	@Override
	protected EntityType matchNonAlias(String key){ return EntityType.getElementNamed(key); }

	//@Override
	//protected String getObjectName(ModDamageElement object){ return object.name(); }
}