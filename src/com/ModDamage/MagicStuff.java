package com.ModDamage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;

import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;

public class MagicStuff
{
	static final String obc; // replacement for org.bukkit.craftbukkit
	static final String nms; // replacement for net.minecraft.server


	static
	{
		Server server = Bukkit.getServer();

		Class<?> serverClass = server.getClass(); // org.bukkit.craftbukkit.CraftServer
		obc = serverClass.getPackage().getName();
		
		MethodAccess server_m = MethodAccess.get(serverClass);
		Object console = server_m.invoke(server, "getServer");

		Class<?> consoleClass = console.getClass(); // net.minecraft.server.MinecraftServer
		nms = consoleClass.getPackage().getName();
	}

	// //////////// Helpers ////////////////
	public static Class<?> safeClassForName(String name)
	{
		try
		{
			return Class.forName(name);
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("ModDamage is out of date! Error: " + e);
		}
		return null;
	}
	public static Field safeGetField(Class<?> cls, String name)
	{
		try
		{
			Field field = cls.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		}
		catch (Exception e)
		{
			System.err.println("ModDamage is out of date! Error: " + e);
		}
		return null;
	}
	public static Object safeGet(Object obj, Field field)
	{
		try
		{
			return field.get(obj);
		}
		catch (Exception e)
		{
			System.err.println("ModDamage is out of date! Error: " + e);
		}
		return null;
	}
	public static Method safeGetMethod(Class<?> cls, String name)
	{
		try
		{
			Method method = cls.getDeclaredMethod(name);
			method.setAccessible(true);
			return method;
		}
		catch (Exception e)
		{
			System.err.println("ModDamage is out of date! Error: " + e);
		}
		return null;
	}
	// ////////// End Helpers ///////////////

	

	////////////////////// getMaxDurability //////////////////////
	
	static final FieldAccess CraftItemStack_f;
	static final int CraftItemStack_handle;

	static final MethodAccess NMSItemStack_m;
	static final int NMSItemStack_getItem;

	static final MethodAccess NMSItem_m;
	static final int NMSItem_getMaxDurability;
	
	static
	{
		CraftItemStack_f = FieldAccess.get(safeClassForName(obc + ".inventory.CraftItemStack"));
		CraftItemStack_handle = CraftItemStack_f.getIndex("handle");

		NMSItemStack_m = MethodAccess.get(safeClassForName(nms + ".ItemStack"));
		NMSItemStack_getItem = NMSItemStack_m.getIndex("getItem");

		NMSItem_m = MethodAccess.get(safeClassForName(nms + ".Item"));
		NMSItem_getMaxDurability = NMSItem_m.getIndex("getMaxDurability");
	}
	
	public static int getMaxDurability(ItemStack itemStack)
	{
		Object handle = CraftItemStack_f.get(itemStack, CraftItemStack_handle);
		if (handle == null) return 0;

		Object item = NMSItemStack_m.invoke(handle, NMSItemStack_getItem);
		if (item == null) return 0;

		Object durability = NMSItem_m.invoke(item, NMSItem_getMaxDurability);
		if (durability == null || !(durability instanceof Integer)) return 0;

		return (Integer) durability;
	}

	////////////////////// end getMaxDurability //////////////////////
	
	
	

	////////////////////// getGroundBlock //////////////////////
	
	static final Method CraftArrow_getHandle;
	
	static final Field NMSEntityArrow_inGround;
	static final Field NMSEntityArrow_x;
	static final Field NMSEntityArrow_y;
	static final Field NMSEntityArrow_z;
	
	static
	{
		Class<?> CraftArrow = safeClassForName(obc + ".entity.CraftArrow");
		CraftArrow_getHandle = safeGetMethod(CraftArrow, "getHandle");
		
		Class<?> NMSEntityArrow = safeClassForName(nms + ".EntityArrow");
		NMSEntityArrow_inGround = safeGetField(NMSEntityArrow, "inGround");
		NMSEntityArrow_x = safeGetField(NMSEntityArrow, "d");
		NMSEntityArrow_y = safeGetField(NMSEntityArrow, "e");
		NMSEntityArrow_z = safeGetField(NMSEntityArrow, "f");
	}
	
	public static Block getGroundBlock(Arrow arrow)
	{
		try
		{
			Object handle = CraftArrow_getHandle.invoke(arrow);
			
			boolean inGround = NMSEntityArrow_inGround.getBoolean(handle);
			if (!inGround) return null;
			
			int x = NMSEntityArrow_x.getInt(handle);
			int y = NMSEntityArrow_y.getInt(handle);
			int z = NMSEntityArrow_z.getInt(handle);
			
			return arrow.getWorld().getBlockAt(x, y, z);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	////////////////////// end getGroundBlock //////////////////////
	
	
	////////////////////// getCommandMap //////////////////////
	
	static final MethodAccess CraftServer_m;
	static final int CraftServer_getCommandMap;
	
	static
	{
		Server server = Bukkit.getServer();

		Class<?> serverClass = server.getClass(); // org.bukkit.craftbukkit.CraftServer
		CraftServer_m = MethodAccess.get(serverClass);
		CraftServer_getCommandMap = CraftServer_m.getIndex("getCommandMap");
	}
	
	public static SimpleCommandMap getCommandMap()
	{
		Server server = Bukkit.getServer();
		
		return (SimpleCommandMap) CraftServer_m.invoke(server, CraftServer_getCommandMap);
	}
	
	////////////////////// end getCommandMap //////////////////////
}
