package com.ModDamage.Backend.Aliasing;

import java.util.Collection;

import com.ModDamage.Backend.EntityType;
import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

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