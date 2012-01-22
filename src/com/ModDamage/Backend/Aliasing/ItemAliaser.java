package com.ModDamage.Backend.Aliasing;

import java.util.Collection;

import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class ItemAliaser extends CollectionAliaser<ModDamageItemStack> 
{
	static ItemAliaser aliaser = new ItemAliaser();
	public static Collection<ModDamageItemStack> match(String string) { return aliaser.matchAlias(string); }
	
	public ItemAliaser() {super(AliasManager.Item.name());}
	@Override
	protected ModDamageItemStack matchNonAlias(String key){ return ModDamageItemStack.getNew(key);}
	@Override
	protected String getObjectName(ModDamageItemStack object){ return object.toString();}
}