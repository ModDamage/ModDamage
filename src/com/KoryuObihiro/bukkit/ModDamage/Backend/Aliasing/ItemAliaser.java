package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageItemStack;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class ItemAliaser extends CollectionAliaser<ModDamageItemStack> 
{
	public ItemAliaser() {super(AliasManager.Item.name());}
	@Override
	protected ModDamageItemStack matchNonAlias(String key){ return ModDamageItemStack.getNew(key);}
	@Override
	protected String getObjectName(ModDamageItemStack object){ return object.toString();}

}