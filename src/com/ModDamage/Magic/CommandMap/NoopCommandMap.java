package com.ModDamage.Magic.CommandMap;

import org.bukkit.command.SimpleCommandMap;


public class NoopCommandMap implements IMagicCommandMap
{
	@Override
	public SimpleCommandMap getCommandMap()
	{
		return null;
	}

}
