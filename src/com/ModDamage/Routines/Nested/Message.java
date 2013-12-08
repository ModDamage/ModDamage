package com.ModDamage.Routines.Nested;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Alias.MessageAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routine;

public class Message extends Routine 
{
	private final Collection<IDataProvider<String>> messages;
	private final MessageTarget messageTarget;
	
	private Message(ScriptLine scriptLine, MessageTarget messageTarget, Collection<IDataProvider<String>> messages)
	{
		super(scriptLine);
		this.messageTarget = messageTarget;
		this.messages = messages;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
		String[] msgs = new String[messages.size()];
		int i = 0;
		for(IDataProvider<String> message :  messages)
			msgs[i++] = message.get(data);
		
		messageTarget.sendMessages(msgs, data);
	}
	
	private abstract static class MessageTarget
	{
		protected static MessageTarget match(StringMatcher sm, EventInfo info)
		{
			StringMatcher sm2 = sm.spawn();
			if (sm2.matchesFront("console") && targetEndPattern.matcher(sm2.string).lookingAt()) {
				sm2.accept();
				return new MessageTarget()
					{
						@Override
						public void sendMessages(String[] msgs, EventData data)
						{
							Logger log = PluginConfiguration.log;
							for(String msg : msgs)
								log.info(msg);
						}

						@Override public String toString() { return "console"; }
					};
			}
			sm2 = sm.spawn();
			if (sm2.matchesFront("server") && targetEndPattern.matcher(sm2.string).lookingAt()) {
				sm2.accept();
				return new MessageTarget()
					{
						@Override
						public void sendMessages(String[] msgs, EventData data)
						{
							Player[] players = Bukkit.getOnlinePlayers();
							for(Player player : players)
								for(String msg : msgs)
									player.sendMessage(msg);
						}

						@Override public String toString() { return "server"; }
					};
			}
			// try a world first
			{
				final IDataProvider<World> worldDP = DataProvider.parse(info, World.class, sm.spawn(), false, false, targetEndPattern);
				if (worldDP != null) {
					return new MessageTarget()
						{
							@Override
							public void sendMessages(String[] msgs, EventData data) throws BailException
							{
								World world = worldDP.get(data);
								if (world == null) return;
								
								List<Player> players = world.getPlayers();
								for(Player player : players)
									for(String msg : msgs)
										player.sendMessage(msg);
							}
	
							@Override public String toString() { return worldDP.toString(); }
						};
				}
			}
			// otherwise try to find a player
			{
				final IDataProvider<Player> playerDP = DataProvider.parse(info, Player.class, sm.spawn(), false, true, targetEndPattern);
				if(playerDP != null) {
					return new MessageTarget()
					{
						@Override
						public void sendMessages(String[] msgs, EventData data) throws BailException
						{
							Player player = playerDP.get(data);
							if (player == null) return;
							for(String msg : msgs)
								player.sendMessage(msg);
						}
	
						@Override public String toString() { return playerDP.toString(); }
					};
				}
			}
			
			return null;
		}
		
		abstract public void sendMessages(String[] msgs, EventData data) throws BailException;
		abstract public String toString();
	}
	
	public static void registerRoutine()
	{
		Routine.registerRoutine(Pattern.compile("message\\.(.+)", Pattern.CASE_INSENSITIVE), new MessageRoutineFactory());
	}

	public static final Pattern targetEndPattern = Pattern.compile("\\.(_\\w+)|:?\\s+|(?:$)");
	
	protected static class MessageRoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			StringMatcher sm = new StringMatcher(matcher.group(1));
			
			MessageTarget messageTarget = MessageTarget.match(sm, info);
			if(messageTarget == null)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad message target: "+matcher.group(1));
				return null;
			}
			
			Matcher targetEnd = sm.matchFront(targetEndPattern);
			
			if (targetEnd.group(1) != null) {
				Collection<IDataProvider<String>> messages = MessageAliaser.match(targetEnd.group(1), info);
				if (messages == null)
				{
//					ModDamage.addToLogRecord(OutputPreset.FAILURE, "This message form can only be used for message aliases. Please use the following instead.");
//					ModDamage.addToLogRecord(OutputPreset.FAILURE, "    message."+matcher.group(1)+": '" + matcher.group(2) + "'");
					return null;
				}
				
				
				Message routine = new Message(scriptLine, messageTarget, messages);
				ModDamage.addToLogRecord(OutputPreset.INFO, "Message (" + messageTarget + "):" );
				ModDamage.changeIndentation(true);
				for (IDataProvider<String> msg : messages)
				{
					ModDamage.addToLogRecord(OutputPreset.INFO, msg.toString());
				}
				ModDamage.changeIndentation(false);
				return new RoutineBuilder(routine);
			}
			

			ModDamage.addToLogRecord(OutputPreset.INFO, "Message (" + messageTarget + "):" );
			ModDamage.changeIndentation(true);
			
			MessageRoutineBuilder builder = new MessageRoutineBuilder(scriptLine, messageTarget, info);
			
			if (!sm.string.isEmpty())
				builder.addString(sm.string);
			
			return builder;
		}
	}

	
	private static class MessageRoutineBuilder implements IRoutineBuilder, ScriptLineHandler
	{
		ScriptLine scriptLine;
		MessageTarget messageTarget;
		EventInfo info;
		
		List<IDataProvider<String>> messages = new ArrayList<IDataProvider<String>>();
		
		public MessageRoutineBuilder(ScriptLine scriptLine, MessageTarget messageTarget, EventInfo info)
		{
			this.scriptLine = scriptLine;
			this.messageTarget = messageTarget;
			this.info = info;
		}
		
		public void addString(String str)
		{
			IDataProvider<String> msgDP = DataProvider.parse(info, String.class, str);
			if (msgDP != null) {
				messages.add(msgDP);
				ModDamage.addToLogRecord(OutputPreset.INFO, msgDP.toString());
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
			return new Message(scriptLine, messageTarget, messages);
		}
	}
}