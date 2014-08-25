package com.ModDamage.Routines.Nested;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import com.ModDamage.LogUtil;
import com.ModDamage.ModDamage;
import com.ModDamage.Alias.CommandAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routine;

public class Command extends NestedRoutine 
{
	private final Collection<IDataProvider<String>> commands;
	private final CommandTarget commandTarget;
	
	private Command(ScriptLine scriptLine, CommandTarget commandTarget, Collection<IDataProvider<String>> commands)
	{
		super(scriptLine);
		this.commandTarget = commandTarget;
		this.commands = commands;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
		CommandSender cmdsender = commandTarget.getCommandSender(data);
		for(IDataProvider<String> command : commands)
		{				
			Bukkit.dispatchCommand(cmdsender, command.get(data));
		}
	}
	
	private abstract static class CommandTarget
	{
		protected static CommandTarget match(ScriptLine scriptLine, String key, EventInfo info)
		{
			if (key.equalsIgnoreCase("console"))
				return new CommandTarget()
					{
						@Override
						public CommandSender getCommandSender(EventData data)
						{
							CommandSender cmdsender = (CommandSender) Bukkit.getConsoleSender();
							return cmdsender;
						}

						@Override public String toString() { return "console"; }
					};
			
			{
				final IDataProvider<Entity> entityDP = DataProvider.parse(scriptLine, info, Entity.class, key);
				if(entityDP == null) return null;
				return new CommandTarget()
				{
					@Override
					public CommandSender getCommandSender(EventData data) throws BailException
					{
						Entity entity = entityDP.get(data);
						if (entity instanceof CommandSender)
						{
							CommandSender cmdsender = (CommandSender) entity;
							return cmdsender;
						}
						return null;
					}

					@Override public String toString() { return entityDP.toString(); }
				};
			}
		}
		
		abstract public CommandSender getCommandSender(EventData data) throws BailException;
		abstract public String toString();
	}
	
	public static void registerRoutine()
	{
		Routine.registerRoutine(Pattern.compile("command\\.(\\w+)\\.(.+)", Pattern.CASE_INSENSITIVE), new CommandAliasRoutineFactory());
	}
	public static void registerNested()
	{
		Routine.registerRoutine(Pattern.compile("command.(\\w+)(?::?\\s+(.+))?", Pattern.CASE_INSENSITIVE), new NestedRoutineFactory());
	}
	
	protected static class CommandAliasRoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			CommandTarget commandTarget = CommandTarget.match(scriptLine, matcher.group(1), info);
			if(commandTarget == null)
			{
				LogUtil.error(scriptLine, "Bad command target: "+matcher.group(1));
				return null;
			}
			
			Collection<IDataProvider<String>> commands = CommandAliaser.match(scriptLine, matcher.group(2), info);
			if (commands == null)
			{
				LogUtil.error("This command form can only be used for command aliases. Please use the following instead.");
				LogUtil.error("    - 'command."+matcher.group(1)+"': '" + matcher.group(2) + "'");
				return null;
			}
			
			
			Command routine = new Command(scriptLine, commandTarget, commands);
			LogUtil.info("Command (" + commandTarget + "):" );
			ModDamage.changeIndentation(true);
			for (IDataProvider<String> cmd : commands)
			{
				LogUtil.info(cmd.toString());
			}
			ModDamage.changeIndentation(false);
			return new RoutineBuilder(routine);
		}
	}
	
	protected static class NestedRoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			CommandTarget commandTarget = CommandTarget.match(scriptLine, matcher.group(1), info);
			if(commandTarget == null) return null;
			

			LogUtil.info("Command (" + commandTarget + "):" );
			ModDamage.changeIndentation(true);
			
			CommandRoutineBuilder builder = new CommandRoutineBuilder(scriptLine, commandTarget, info);
			
			if (matcher.group(2) != null)
				builder.addString(matcher.group(2));
			
			return builder;
		}
	}
	
	private static class CommandRoutineBuilder implements IRoutineBuilder, ScriptLineHandler
	{
		ScriptLine scriptLine;
		CommandTarget commandTarget;
		EventInfo info;
		
		List<IDataProvider<String>> commands = new ArrayList<IDataProvider<String>>();
		
		public CommandRoutineBuilder(ScriptLine scriptLine, CommandTarget commandTarget, EventInfo info)
		{
			this.scriptLine = scriptLine;
			this.commandTarget = commandTarget;
			this.info = info;
		}

		public void addString(String str)
		{
			IDataProvider<String> cmdDP = DataProvider.parse(scriptLine, info, String.class, str);
			if (cmdDP != null) {
				commands.add(cmdDP);
				LogUtil.info(cmdDP.toString());
			}
		}

		@Override
		public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren)
		{
			addString(line.line);
			return null;
		}
		
		@Override
		public void done()
		{
			ModDamage.changeIndentation(false);
		}
		
		@Override
		public ScriptLineHandler getScriptLineHandler()
		{
			return this;
		}
		
		@Override
		public Routine buildRoutine()
		{
			return new Command(scriptLine, commandTarget, commands);
		}
	}
	
}