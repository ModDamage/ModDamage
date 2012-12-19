package com.ModDamage.Events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.ModDamage.MDEvent;
import com.ModDamage.MagicStuff;
import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Utils;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;


public class Command extends MDEvent
{
	public static final Command instance = new Command();
	
	private Command() {super(null);}
	
	Map<String, List<CommandInfo>> commandMap = new HashMap<String, List<CommandInfo>>();
	ArrayList<MDCommand> bukkitCommands = new ArrayList<MDCommand>();
	
	
	@SuppressWarnings("unchecked")
	protected void load(Object commands)
	{
		specificLoadState = LoadState.FAILURE;
		
		boolean failed = false;
		
		SimpleCommandMap cmap = MagicStuff.getCommandMap();
		
		commandMap.clear();
		
		for (org.bukkit.command.Command cmd : bukkitCommands)
		{
			cmap.getCommands().remove(cmd);
			cmd.unregister(cmap);
		}
		bukkitCommands.clear();
		
		//LinkedHashMap<String, Object> entries = ModDamage.getPluginConfiguration().getConfigMap();
		//Object commands = PluginConfiguration.getCaseInsensitiveValue(entries, "Command");
		
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
			Argument[] args;
			boolean catchAll = false;
            String catchAllName = null;
			
			if (commandSpec.length > 1 && commandSpec[commandSpec.length-1].startsWith("*"))
			{
				catchAll = true;
                if (commandSpec[commandSpec.length-1].length() > 1)
                    catchAllName = commandSpec[commandSpec.length-1].substring(1);
                args = new Argument[commandSpec.length - 2];
			}
			else
				args = new Argument[commandSpec.length - 1];
			
			StringBuilder logSB = new StringBuilder();
			
			for (int i = 1; i < commandSpec.length - (catchAll?1:0); i++)
			{				
				Argument arg = Argument.get(commandSpec[i]);
				if (arg == null) {
					ModDamage.addToLogRecord(OutputPreset.FAILURE, 
							"Please prefix command arguments with # for number or & for player, or [a-z] for raw, not "
							+commandSpec[i].substring(0, 1));
					failed = true;
					continue entryLoop;
				}
				args[i-1] = arg;
				logSB.append(" "+arg.name+"("+arg.type+")");
			}
			if (catchAll) {
				logSB.append(" *");
                if (catchAllName != null)
                    logSB.append(catchAllName);
            }
			
			CommandInfo command = new CommandInfo(name, args, catchAll, catchAllName);
			ModDamage.addToLogRecord(OutputPreset.INFO, "Command ["+command.name+"]: "+logSB.toString());
			command.routines = RoutineAliaser.parseRoutines(commandEntry.getValue(), command.eventInfo);
			if (command.routines == null)
			{
				failed = true;
				continue;
			}
			
			List<CommandInfo> cmds = commandMap.get(name);
			if (cmds == null)
			{
				cmds = new ArrayList<CommandInfo>();
				commandMap.put(name, cmds);
			}
			cmds.add(command);
			
			if (cmds.size() == 1) {
				MDCommand mdcommand = new MDCommand(command.name);
				cmap.register("md", mdcommand);
				bukkitCommands.add(mdcommand);
			}
		}

		ModDamage.changeIndentation(false);
		
		if (!failed) specificLoadState = LoadState.SUCCESS;
	}
	
	static class MDCommand extends org.bukkit.command.Command implements PluginIdentifiableCommand
	{
		public MDCommand(String name)
		{
			super(name, "", name, new ArrayList<String>());
		}

		@Override
		public Plugin getPlugin()
		{
			return ModDamage.getPluginConfiguration().plugin;
		}

		@Override
		public boolean execute(CommandSender sender, String commandLabel, String[] args)
		{
			String[] oldargs = args;
			args = new String[args.length + 1];
			for (int i = 0; i < oldargs.length; i++)
				args[i+1] = oldargs[i];
			args[0] = commandLabel;
			boolean success = CommandEventHandler.handleCommand(sender, args);
			if (!success) 
		        sender.sendMessage("Unknown command. Type \"help\" for help.");
			return success;
		}
	}
	
	
	static class CommandInfo
	{
		String name;
		Argument[] args;
		
		EventInfo eventInfo;
		Routines routines;
		
		boolean catchAll;
        String catchAllName;
		
		public CommandInfo(String name, Argument[] args, boolean catchAll, String catchAllName)
		{
			this.name = name;
			this.args = args;
			this.catchAll = catchAll;
            this.catchAllName = catchAllName;
			
			// build info list for my eventInfo object
			List<Object> infoList = new ArrayList<Object>(2*args.length + 4);
			infoList.add(Player.class);
			infoList.add("sender");
			infoList.add(World.class);
			infoList.add("world");
			
			for (Argument arg : args)
				arg.addToEventInfoList(infoList);
            if (catchAll && catchAllName != null) {
                infoList.add(String.class);
                infoList.add(catchAllName);
            }
			
			eventInfo = new SimpleEventInfo(infoList.toArray(), false);
		}
	}
	
	static abstract class Argument
	{
		String type;
		String name;
		
		public Argument(String name, String type)
		{
			this.name = name;
			this.type = type;
		}
		
		public static Argument get(String string)
		{
			if (string.startsWith("&"))
				return new Argument(string.substring(1), "Player") {
					@Override
					public boolean addToEventDataList(List<Object> dataList, String arg)
					{
						Player player = Bukkit.getPlayer(arg);
						if (player == null) return false;
						dataList.add(player);
						return true;
					}
					public void addToEventInfoList(List<Object> list)
					{
						list.add(Player.class);
						super.addToEventInfoList(list);
					}
					
				};
			if (string.startsWith("#"))
				return new Argument(string.substring(1), "Number") {
					@Override
					public boolean addToEventDataList(List<Object> dataList, String arg)
					{
						try {
							Integer integer = Integer.parseInt(arg);
							dataList.add(integer);
							return true;
						}
						catch (NumberFormatException e) { }
						return false;
					}
					public void addToEventInfoList(List<Object> list)
					{
						list.add(Integer.class);
						super.addToEventInfoList(list);
					}
				};
            if (string.startsWith("%"))
                return new Argument(string.substring(1), "Word") {
                    @Override
                    public boolean addToEventDataList(List<Object> dataList, String arg)
                    {
                        dataList.add(arg);
                        return true;
                    }
                    public void addToEventInfoList(List<Object> list)
                    {
                        list.add(String.class);
                        super.addToEventInfoList(list);
                    }
                };
			if (string.matches("^[a-zA-Z].*"))
				return new Argument(string, "RawWord") {
					@Override
					public boolean addToEventDataList(List<Object> dataList, String arg)
					{
						return arg.equalsIgnoreCase(name);
					}
					public void addToEventInfoList(List<Object> list)
					{
					}
				};
			return null;
		}

		public abstract boolean addToEventDataList(List<Object> dataList, String arg);
		
		public void addToEventInfoList(List<Object> list)
		{
			list.add(name);
		}
	}
	
	public static class CommandEventHandler
	{
		public static boolean handleCommand(CommandSender sender, String[] words)
		{
			if (words.length == 0) return false;
			
			List<CommandInfo> commands = instance.commandMap.get(words[0]);
			if (commands == null) return false;
			commandLoop: for (CommandInfo cmd : commands)
			{
				if (!(cmd.catchAll? words.length - 1 >= cmd.args.length : words.length - 1 == cmd.args.length))
					continue;
				
				List<Object> dataArgs = new ArrayList<Object>(cmd.args.length + 1); // estimate
				dataArgs.add(sender instanceof Player? (Player) sender : null);
				dataArgs.add(sender instanceof Player? ((Player)sender).getWorld() : null);
				
				for (int i = 1; i < words.length; i++)
				{
					if (i-1 >= cmd.args.length)
						break;
					
					if (!cmd.args[i-1].addToEventDataList(dataArgs, words[i]))
						continue commandLoop;
				}

                if (cmd.catchAll && cmd.catchAllName != null) {
                    dataArgs.add(Utils.joinBy(" ", Arrays.copyOfRange(words, cmd.args.length+1, words.length)));
                }
				
				EventData data = cmd.eventInfo.makeData(dataArgs.toArray(), false);
				try
				{
					if (cmd.routines != null)
						cmd.routines.run(data);
				}
				catch (BailException e)
				{
					ModDamage.reportBailException(e);
				}
				
				return true;
			}
			
			return false;
		}
	}
}
