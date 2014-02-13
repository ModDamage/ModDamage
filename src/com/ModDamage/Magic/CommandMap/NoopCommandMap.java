package com.ModDamage.Magic.CommandMap;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;


public class NoopCommandMap implements IMagicCommandMap
{
	@Override
	public SimpleCommandMap getCommandMap()
	{
		return null;
	}
	
	@Override
	public Map<String, Command> getKnownCommandsRawMap()
	{
		return null;
	}

}
