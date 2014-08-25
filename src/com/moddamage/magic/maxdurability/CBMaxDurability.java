package com.moddamage.magic.maxdurability;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

import com.moddamage.magic.MagicStuff;

public class CBMaxDurability implements IMagicMaxDurability
{
	final Field CraftItemStack_handle;

	final Method NMSItemStack_getItem;

	final Method NMSItem_getMaxDurability;
	
	public CBMaxDurability()
	{
		
		Class<?> CraftItemStack = MagicStuff.safeClassForName(MagicStuff.obc + ".inventory.CraftItemStack");
		CraftItemStack_handle = MagicStuff.safeGetField(CraftItemStack, "handle");
		
		NMSItemStack_getItem = MagicStuff.safeGetMethod(MagicStuff.safeClassForName(MagicStuff.nms + ".ItemStack"), "getItem");
		
		NMSItem_getMaxDurability = MagicStuff.safeGetMethod(MagicStuff.safeClassForName(MagicStuff.nms + ".Item"), "getMaxDurability");
		
	}
	
	public int getMaxDurability(ItemStack itemStack)
	{
		Object handle = MagicStuff.safeGet(itemStack, CraftItemStack_handle);
		if (handle == null) return 0;
		
		Object item = MagicStuff.safeInvoke(handle, NMSItemStack_getItem);
		if (item == null) return 0;

		Object durability = MagicStuff.safeInvoke(item, NMSItem_getMaxDurability);
		if (durability == null || !(durability instanceof Integer)) return 0;

		return (Integer) durability;
	}
}
