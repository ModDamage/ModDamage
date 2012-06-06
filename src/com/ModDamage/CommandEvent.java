package com.ModDamage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;


public class CommandEvent
{
	static Map<String, List<Command>> commandMap = new HashMap<String, List<Command>>();
	
	@SuppressWarnings("unchecked")
	public static void reload()
	{
		commandMap.clear();
		
		LinkedHashMap<String, Object> entries = ModDamage.getPluginConfiguration().getConfigMap();
		Object commands = PluginConfiguration.getCaseInsensitiveValue(entries, "Command");
		
		if(commands == null)
			return;
	
		if (!(commands instanceof List))
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Expected List, got "+commands.getClass().getSimpleName()+"for Command event");
			return;
		}
		
		List<LinkedHashMap<String, Object>> commandConfigMaps = (List<LinkedHashMap<String, Object>>) commands;
		if(commandConfigMaps == null || commandConfigMaps.size() == 0)
			return;
		
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Loading commands...");
		
		ModDamage.changeIndentation(true);
		
		entryLoop: for (LinkedHashMap<String, Object> commandConfigMap : commandConfigMaps)
		for (Entry<String, Object> commandEntry : commandConfigMap.entrySet())
		{
			String[] commandSpec = commandEntry.getKey().split("\\s+");
			String name = commandSpec[0];
			Argument[] args = new Argument[commandSpec.length - 1];
			boolean catchAll = false;
			
			StringBuilder logSB = new StringBuilder();
			
			for (int i = 1; i < commandSpec.length; i++)
			{
				if (i == commandSpec.length - 1 && commandSpec[i].equals("*"))
				{
					logSB.append(" *");
					catchAll = true;
					continue;
				}
				
				ArgumentType type = ArgumentType.get(commandSpec[i].substring(0, 1));
				if (type == null) {
					ModDamage.addToLogRecord(OutputPreset.FAILURE, 
							"Please prefix command arguments with # for number or & for player, not "
							+commandSpec[i].substring(0, 1));
					continue entryLoop;
				}
				args[i-1] = new Argument(commandSpec[i].substring(1), type);
				logSB.append(" "+args[i-1].name+"("+type.name()+")");
			}
			
			Command command = new Command(name, args, catchAll);
			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Command ["+command.name+"]: "+logSB.toString());
			command.routines = RoutineAliaser.parseRoutines(commandEntry.getValue(), command.eventInfo);
			if (command.routines == null)
				continue;
			
			List<Command> cmds = commandMap.get(name);
			if (cmds == null)
			{
				cmds = new ArrayList<Command>();
				commandMap.put(name, cmds);
			}
			cmds.add(command);
			
		}
	}
	
	
	static class Command
	{
		String name;
		Argument[] args;
		
		EventInfo eventInfo;
		Routines routines;
		
		boolean catchAll;
		
		public Command(String name, Argument[] args, boolean catchAll)
		{
			this.name = name;
			this.args = args;
			this.catchAll = catchAll;
			
			// build info list for my eventInfo object
			Object[] infoArr = new Object[2*(args.length + 2)];
			infoArr[0] = Player.class;
			infoArr[1] = "sender";
			infoArr[2] = World.class;
			infoArr[3] = "world";
			
			
			int i = 2;
			for (Argument arg : args)
			{
				infoArr[2*i+0] = arg.type.infoCls;
				infoArr[2*i+1] = arg.name;
			}
			
			//ModDamage.addToLogRecord(OutputPreset.INFO, "INFOARR: "+Utils.joinBy(", ", infoArr));
			
			eventInfo = new SimpleEventInfo(infoArr, false);
		}
	}
	
	static enum ArgumentType
	{
		Player("&", Player.class) {
				@Override
				public Object parseArgument(String arg)
				{
					return Bukkit.getPlayer(arg);
				}
			},
		Number("#", IntRef.class) {
				@Override
				public Object parseArgument(String arg)
				{
					try
					{
						return new IntRef(Integer.parseInt(arg));
					}
					catch (NumberFormatException e)
					{
						return null;
					}
				}
			};
		
		String prefix;
		Class<?> infoCls;
		
		private ArgumentType(String prefix, Class<?> infoCls)
		{
			this.prefix = prefix;
			this.infoCls = infoCls;
		}
		
		public static ArgumentType get(String prefix)
		{
			for (ArgumentType type : values())
			{
				if (type.prefix.equals(prefix))
					return type;
			}
			return null;
		}
		
		public abstract Object parseArgument(String arg);
	}
	
	static class Argument
	{
		String name;
		ArgumentType type;
		
		public Argument(String name, ArgumentType type)
		{
			this.name = name;
			this.type = type;
		}
	}
	
	static class CommandEventHandler implements Listener
	{
		@EventHandler(priority=EventPriority.LOW)
		public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event)
		{
			if (event.isCancelled()) return;
			
			String[] words = event.getMessage().split("\\s+");
			if (words.length == 0) return;
			
			List<Command> commands = commandMap.get(words[0]);
			commandLoop: for (Command cmd : commands)
			{
				if (!(cmd.catchAll? words.length >= cmd.args.length : words.length == cmd.args.length))
					continue;
				
				Object[] dataArgs = new Object[cmd.args.length + 2];
				dataArgs[0] = event.getPlayer();
				dataArgs[1] = event.getPlayer().getWorld();
				
				for (int i = 0; i < words.length; i++)
				{
					Object obj = cmd.args[i].type.parseArgument(words[i]);
					if (obj == null)
						continue commandLoop;
					
					dataArgs[i+2] = obj;
				}
				
				EventData data = cmd.eventInfo.makeData(dataArgs, false);
				try
				{
					if (cmd.routines != null)
						cmd.routines.run(data);
				}
				catch (BailException e)
				{
					ModDamage.reportBailException(e);
				}
				
				event.setCancelled(true);
				
				return;
			}
			
		}
	}
}
