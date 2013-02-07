package com.ModDamage.Magic.MaxDurability;

import java.lang.reflect.Field;

import org.bukkit.inventory.ItemStack;

import com.ModDamage.Magic.MagicStuff;
import com.esotericsoftware.reflectasm.MethodAccess;

public class CBMaxDurability implements IMagicMaxDurability
{
	final Field CraftItemStack_handle;

	final MethodAccess NMSItemStack_m;
	final int NMSItemStack_getItem;

	final MethodAccess NMSItem_m;
	final int NMSItem_getMaxDurability;
	
	public CBMaxDurability()
	{
		Class<?> CraftItemStack = MagicStuff.safeClassForName(MagicStuff.obc + ".inventory.CraftItemStack");
		CraftItemStack_handle = MagicStuff.safeGetField(CraftItemStack, "handle");
		
		NMSItemStack_m = MethodAccess.get(MagicStuff.safeClassForName(MagicStuff.nms + ".ItemStack"));
		NMSItemStack_getItem = NMSItemStack_m.getIndex("getItem");

		NMSItem_m = MethodAccess.get(MagicStuff.safeClassForName(MagicStuff.nms + ".Item"));
		NMSItem_getMaxDurability = NMSItem_m.getIndex("getMaxDurability");
	}
	
	public int getMaxDurability(ItemStack itemStack)
	{
		Object handle = MagicStuff.safeGet(itemStack, CraftItemStack_handle);
		if (handle == null) return 0;

		Object item = NMSItemStack_m.invoke(handle, NMSItemStack_getItem);
		if (item == null) return 0;

		Object durability = NMSItem_m.invoke(item, NMSItem_getMaxDurability);
		if (durability == null || !(durability instanceof Integer)) return 0;

		return (Integer) durability;
	}
}
