package com.ModDamage.Backend.Aliasing;

import java.util.Collection;

import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class TypeAliaser extends CollectionAliaser<ModDamageElement> 
{
	public static TypeAliaser aliaser = new TypeAliaser();
	public static Collection<ModDamageElement> match(String string) { return aliaser.matchAlias(string); }
	
	public TypeAliaser() {super(AliasManager.Type.name()); }

	@Override
	protected ModDamageElement matchNonAlias(String key){ return ModDamageElement.getElementNamed(key); }

	//@Override
	//protected String getObjectName(ModDamageElement object){ return object.name(); }
}