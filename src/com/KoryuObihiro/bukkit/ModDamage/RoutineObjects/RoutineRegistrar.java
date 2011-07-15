package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import org.bukkit.plugin.Plugin;

abstract public class RoutineRegistrar 
{
	public final String pluginName;
	public final String version;
	public final String author;
	public RoutineRegistrar(Plugin plugin)
	{
		this.pluginName = plugin.getDescription().getName();
		this.version = plugin.getDescription().getVersion();
		this.author = plugin.getDescription().getAuthors().toString();
	}
	abstract void registerCalculations();
}
