package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageItemStack;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class ItemAliaser extends CollectionAliaser<ModDamageItemStack> 
{
	private static final long serialVersionUID = 1693391099291070076L;

	public ItemAliaser() {super("Item");}
	@Override
	protected ModDamageItemStack matchNonAlias(String key){ return ModDamageItemStack.getNew(key);}
	@Override
	protected String getObjectName(ModDamageItemStack object){ return object.toString();}

}