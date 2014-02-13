package com.ModDamage.Magic.CommandMap;

import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import java.util.Map;

public interface IMagicCommandMap
{
	SimpleCommandMap getCommandMap();

	Map<String, Command> getKnownCommandsRawMap();
}
