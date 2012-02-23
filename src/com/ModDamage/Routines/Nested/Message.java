package com.ModDamage.Routines.Nested;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.Aliasing.AliasManager;
import com.ModDamage.Backend.Aliasing.MessageAliaser;
import com.ModDamage.Backend.Matching.InterpolatedString;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Routines.Routine;

public class Message extends NestedRoutine 
{
	private final Collection<InterpolatedString> messages;
	private final MessageTarget messageTarget;
	
	private Message(String configString, MessageTarget messageTarget, Collection<InterpolatedString> messages)
	{
		super(configString);
		this.messageTarget = messageTarget;
		this.messages = messages;
	}
	
	@Override
	public void run(EventData data)
	{
		List<String> msgs = new ArrayList<String>();
		for(InterpolatedString message :  messages)
			msgs.add(message.toString(data));
		
		messageTarget.sendMessages(msgs, data);
	}
	
	private abstract static class MessageTarget
	{
		protected static MessageTarget match(String key, EventInfo info)
		{
			if (key.equalsIgnoreCase("server"))
				return new MessageTarget()
					{
						@Override
						public void sendMessages(Collection<String> msgs, EventData data)
						{
							Player[] players = Bukkit.getOnlinePlayers();
							for(Player player : players)
								for(String msg : msgs)
									player.sendMessage(msg);
						}

						@Override public String toString() { return "server"; }
					};
			if (key.equalsIgnoreCase("world"))
			{
				final DataRef<World> worldRef = info.get(World.class, "world");
				if (worldRef == null) return null;
				return new MessageTarget()
					{
						@Override
						public void sendMessages(Collection<String> msgs, EventData data)
						{
							List<Player> players = worldRef.get(data).getPlayers();
							for(Player player : players)
								for(String msg : msgs)
									player.sendMessage(msg);
						}

						@Override public String toString() { return "world"; }
					};
			}
			
			{
				final DataRef<Entity> entityRef = info.get(Entity.class, key);
				if(entityRef == null) return null;
				return new MessageTarget()
				{
					@Override
					public void sendMessages(Collection<String> msgs, EventData data)
					{
						Entity entity = entityRef.get(data);
						if (entity instanceof Player)
						{
							Player player = (Player) entity;
							for(String msg : msgs)
								player.sendMessage(msg);
						}
					}

					@Override public String toString() { return entityRef.toString(); }
				};
			}
		}
		
		abstract public void sendMessages(Collection<String> msgs, EventData data);
		abstract public String toString();
	}
	
	public static void registerRoutine()
	{
		Routine.registerRoutine(Pattern.compile("message\\.(\\w+)\\.(.+)", Pattern.CASE_INSENSITIVE), new BaseRoutineBuilder());//"debug\\.(server|(?:world(\\.[a-z0-9]+))|(?:player\\.[a-z0-9]+))\\.(_[a-z0-9]+)"
	}
	public static void registerNested()
	{
		NestedRoutine.registerRoutine(Pattern.compile("message.(\\w+)", Pattern.CASE_INSENSITIVE), new NestedRoutineBuilder());
	}
	
	protected static class BaseRoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Message getNew(Matcher matcher, EventInfo info)
		{
			MessageTarget messageTarget = MessageTarget.match(matcher.group(1), info);
			if(messageTarget == null)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad message target: "+matcher.group(1));
				return null;
			}
			
			Collection<InterpolatedString> messages = MessageAliaser.match(matcher.group(2), info);
			if (messages == null)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "This message form can only be used for message aliases. Please use the following instead.");
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "    - 'message."+matcher.group(1)+"': '" + matcher.group(2) + "'");
				return null;
			}
			
			
			Message routine = new Message(matcher.group(), messageTarget, messages);
			routine.reportContents();
			return routine;
		}
	}
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@SuppressWarnings("unchecked")
		@Override
		public Message getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			if(matcher == null || nestedContent == null)
				return null;
			
			List<String> strings = new ArrayList<String>();
			MessageTarget messageTarget = MessageTarget.match(matcher.group(1), info);
			if(messageTarget == null) return null;
			
			if (nestedContent instanceof String)
				strings.add((String)nestedContent);
			else if(nestedContent instanceof List)
				strings.addAll((List<String>) nestedContent);
			else
				return null;
			

			List<InterpolatedString> messages = new ArrayList<InterpolatedString>();
			for(String string : strings)
			{
				if (AliasManager.aliasPattern.matcher(string).matches())
				{
					Collection<InterpolatedString> istrs = MessageAliaser.match(string, info);
					if (istrs != null) 
					{
						messages.addAll(istrs);
						continue;
					}
					
					ModDamage.addToLogRecord(OutputPreset.WARNING, "Unknown message alias: "+string);
				}
				
				messages.add(new InterpolatedString(string, info, true));
			}
			
			
			Message routine = new Message(matcher.group(), messageTarget, messages);
			routine.reportContents();
			return routine;
		}
	}
	
	private void reportContents()
	{
		if(messages instanceof List)
		{
			String routineString = "Message (" + messageTarget + ")";
			List<InterpolatedString> messageList = (List<InterpolatedString>)messages;
			if(messages.size() > 1)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, routineString + ":" );
				ModDamage.changeIndentation(true);
				for(int i = 0; i < messages.size(); i++)
					ModDamage.addToLogRecord(OutputPreset.INFO, "- \"" + messageList.get(i).toString() + "\"" );
				ModDamage.changeIndentation(false);
			}
			else ModDamage.addToLogRecord(OutputPreset.INFO, routineString + ": \"" + messageList.get(0).toString() + "\"" );
		}
		else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Fatal: messages are not in a linked data structure!");//shouldn't happen
	}
}