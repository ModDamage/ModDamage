package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import org.bukkit.plugin.Plugin;

abstract public class ModDamageCalculationRegistrar 
{
	public final String pluginName;
	public final String version;
	public final String author;
	public ModDamageCalculationRegistrar(Plugin plugin)
	{
		this.pluginName = plugin.getDescription().getName();
		this.version = plugin.getDescription().getVersion();
		this.author = plugin.getDescription().getAuthors().toString();
	}
	abstract void registerCalculations();
}
