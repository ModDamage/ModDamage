package com.ModDamage.Magic.CommandMap;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.SimpleCommandMap;

import com.ModDamage.Magic.MagicStuff;

public class CBCommandMap implements IMagicCommandMap
{
	final Method CraftServer_getCommandMap;
	
	public CBCommandMap()
	{
		Server server = Bukkit.getServer(); // org.bukkit.craftbukkit.CraftServer

		CraftServer_getCommandMap = MagicStuff.safeGetMethod(server.getClass(), "getCommandMap");
	}

	@Override
	public SimpleCommandMap getCommandMap()
	{
		Server server = Bukkit.getServer();
		return (SimpleCommandMap) MagicStuff.safeInvoke(server, CraftServer_getCommandMap);
	}
}
