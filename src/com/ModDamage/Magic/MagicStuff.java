package com.ModDamage.Magic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.Magic.CommandMap.CBCommandMap;
import com.ModDamage.Magic.CommandMap.IMagicCommandMap;
import com.ModDamage.Magic.CommandMap.NoopCommandMap;
import com.ModDamage.Magic.GroundBlock.CBGroundBlock;
import com.ModDamage.Magic.GroundBlock.IMagicGroundBlock;
import com.ModDamage.Magic.GroundBlock.NoopGroundBlock;
import com.ModDamage.Magic.MaxDurability.CBMaxDurability;
import com.ModDamage.Magic.MaxDurability.IMagicMaxDurability;
import com.ModDamage.Magic.MaxDurability.NoopMaxDurability;
import com.esotericsoftware.reflectasm.MethodAccess;

public class MagicStuff
{
	// simple package names for version immunity
	public static final String obc; // replacement for org.bukkit.craftbukkit
	public static final String nms; // replacement for net.minecraft.server


	static
	{
		Server server = null;
		Class<?> serverClass = null;
		String orgBukkitCraftBukkit = null;
		
		try {
			server = Bukkit.getServer();
	
			serverClass = server.getClass(); // org.bukkit.craftbukkit.CraftServer
			orgBukkitCraftBukkit = serverClass.getPackage().getName();
		}
		catch (Exception e) {
			System.err.println("Magic load error 1");
			e.printStackTrace();
		}
		
		obc = orgBukkitCraftBukkit;
		
		
		
		String netMinecraftServer = null;
		
		try {
			MethodAccess server_m = MethodAccess.get(serverClass);
			Object console = server_m.invoke(server, "getServer");
	
			Class<?> consoleClass = console.getClass(); // net.minecraft.server.MinecraftServer
			netMinecraftServer = consoleClass.getPackage().getName();
		}
		catch (Exception e) {
			System.err.println("Magic load error 2");
			e.printStackTrace();
		}
		
		nms = netMinecraftServer;
		
		
		
		initMaxDurability();
		initGroundBlock();
		initCommandMap();
	}

	
	
	

	////////////////////// getMaxDurability //////////////////////
	
	static IMagicMaxDurability magicMaxDurability = null;
	static boolean maxDurabilityLoaded = false;
	
	private static void initMaxDurability(){
		try { magicMaxDurability = new CBMaxDurability(); }
		catch (Exception e) { }
		
		if (magicMaxDurability == null) {
			System.err.println("Failed to load MaxDurability magic. _maxdurability will be 0!");
			magicMaxDurability = new NoopMaxDurability();
		}
		else {
			System.out.println("MaxDurability magic loaded successfully");
			maxDurabilityLoaded = true;
		}
	}
	
	public static int getMaxDurability(ItemStack itemStack) {
		return magicMaxDurability.getMaxDurability(itemStack);
	}

	////////////////////// end getMaxDurability //////////////////////
	
	
	

	////////////////////// getGroundBlock //////////////////////
	
	static IMagicGroundBlock magicGroundBlock = null;
	static boolean groundBlockLoaded = false;
	
	
	private static void initGroundBlock() {
		try { magicGroundBlock = new CBGroundBlock(); }
		catch (Exception e) { }
		
		if (magicGroundBlock == null) {
			System.err.println("Failed to load GroundBlock magic. hitblock in ProjectileHit will be null!");
			magicGroundBlock = new NoopGroundBlock();
		}
		else {
			System.out.println("GroundBlock magic loaded successfully");
			groundBlockLoaded = true;
		}
	}
	
	public static Block getGroundBlock(Arrow arrow)
	{
		return magicGroundBlock.getGroundBlock(arrow);
	}

	////////////////////// end getGroundBlock //////////////////////
	
	
	
	////////////////////// getCommandMap //////////////////////
	
	static IMagicCommandMap magicCommandMap = null;
	static boolean commandMapLoaded = false;
	
	
	private static void initCommandMap() {
		try { magicCommandMap = new CBCommandMap(); }
		catch (Exception e) { }
		
		if (magicCommandMap == null) {
			System.err.println("Failed to load CommandMap magic. Command event will not work!");
			magicCommandMap = new NoopCommandMap();
		}
		else {
			System.out.println("CommandMap magic loaded successfully");
			commandMapLoaded = true;
		}
	}
	
	public static SimpleCommandMap getCommandMap()
	{
		return magicCommandMap.getCommandMap();
	}
	
	
	////////////////////// end getCommandMap //////////////////////
	
	
	
	
	////////////////////// Safe Reflection Helpers //////////////////////

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
}
