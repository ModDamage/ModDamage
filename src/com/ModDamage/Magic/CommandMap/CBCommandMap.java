package com.ModDamage.Magic.CommandMap;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.SimpleCommandMap;

import com.esotericsoftware.reflectasm.MethodAccess;

public class CBCommandMap implements IMagicCommandMap
{
	final MethodAccess CraftServer_m;
	final int CraftServer_getCommandMap;
	
	public CBCommandMap()
	{
		Server server = Bukkit.getServer();

		Class<?> serverClass = server.getClass(); // org.bukkit.craftbukkit.CraftServer
		CraftServer_m = MethodAccess.get(serverClass);
		CraftServer_getCommandMap = CraftServer_m.getIndex("getCommandMap");
	}

	@Override
	public SimpleCommandMap getCommandMap()
	{
		Server server = Bukkit.getServer();
		
		return (SimpleCommandMap) CraftServer_m.invoke(server, CraftServer_getCommandMap);
	}
}
