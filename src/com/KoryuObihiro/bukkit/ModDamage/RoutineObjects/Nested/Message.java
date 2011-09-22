package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.NestedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Message extends NestedRoutine 
{
	protected static final Pattern nestedPattern = Pattern.compile("message.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	//FIXME Need to implement a special class that contains references and does .toString(), for dynamic referencing. Also, need color.
	protected final EntityReference entityReference;
	protected final List<DynamicMessage> message;
	protected final MessageType messageType;
	public Message(String configString, EntityReference entityReference, List<DynamicMessage> messages)
	{
		super(configString);
		this.messageType = MessageType.ENTITY;
		this.entityReference = entityReference;
		this.message = messages;
	}
	public Message(String configString, MessageType messageType, List<DynamicMessage> messages)
	{
		super(configString);
		this.messageType = messageType;
		this.entityReference = null;
		this.message = messages;
	}
	@Override
	public void run(TargetEventInfo eventInfo)
	{ 
		messageType.sendMessage(entityReference, eventInfo, message);
	}
	
	public static void register()
	{
		Routine.registerBase(Message.class, Pattern.compile("message\\.(\\w+)\\.(.*)", Pattern.CASE_INSENSITIVE));//"debug\\.(server|(?:world(\\.[a-z0-9]+))|(?:player\\.[a-z0-9]+))\\.(_[a-z0-9]+)"
		NestedRoutine.register(Message.class, nestedPattern);
	}
	
	public static Message getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<DynamicMessage> messages = ModDamage.matchMessageAlias(matcher.group(2));
			if(!messages.isEmpty())
			{
				if(EntityReference.isValid(matcher.group(1)))
					return new Message(matcher.group(), EntityReference.match(matcher.group(1)), messages);
				else if(MessageType.match(matcher.group(1)) != null)
					return new Message(matcher.group(), MessageType.match(matcher.group(1)), messages);
				ModDamage.addToLogRecord(DebugSetting.QUIET, "Unrecognized message recipient \"" + matcher.group(1) + "\"", LoadState.FAILURE);
				return null;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Message getNew(String string, Object nestedContent)
	{
		if(string != null && nestedContent != null)
		{
			if(nestedContent instanceof List)
			{
				boolean failFlag = false;
				List<DynamicMessage> messages = new ArrayList<DynamicMessage>();
				for(Object object : (List<Object>)nestedContent)
				{
					if(!(object instanceof String))
					{
						ModDamage.addToLogRecord(DebugSetting.NORMAL, "Unrecognized message element \"" + object.toString() + "\"", LoadState.NOT_LOADED);
						failFlag = true;
					}
					messages.addAll(ModDamage.matchMessageAlias((String)object));
				}
				
				String[] splitParts = string.split("\\.");
				Message routine = null;
				if(EntityReference.isValid(splitParts[1]))
					routine = new Message(string, EntityReference.match(splitParts[1]), messages);
				else if(MessageType.match(splitParts[1]) != null)
					routine = new Message(string, MessageType.match(splitParts[1]), messages);
				if(!failFlag) return routine;
			}	
		}
		return null;
	}
	
	private enum MessageType
	{
		ENTITY, WORLD, SERVER;
		
		private static MessageType match(String key)
		{
			for(MessageType messageType : MessageType.values())
			{
				if(messageType.equals(MessageType.ENTITY)) continue;
				if(key.equalsIgnoreCase(messageType.name()))
					return messageType;
			}
			return null;
		}
		
		private void sendMessage(EntityReference entityReference, TargetEventInfo eventInfo, List<DynamicMessage> messages)
		{
			switch(this)
			{
				case ENTITY:
					if(entityReference.getEntity(eventInfo) instanceof Player)
					{
						Player player = (Player)entityReference.getEntity(eventInfo);
						for(DynamicMessage message : messages)
							message.sendMessage(eventInfo, player);
					}
					break;
				case WORLD:
					for(Player player : eventInfo.world.getPlayers())
						for(DynamicMessage message : messages)
							message.sendMessage(eventInfo, player);
					break;
				case SERVER:
					for(Player player : TargetEventInfo.server.getOnlinePlayers())
						for(DynamicMessage message : messages)
							message.sendMessage(eventInfo, player);
					break;
			}
		}
	}
	
	public static class DynamicMessage
	{
		private final Pattern dynamicPattern = Pattern.compile(".*?%(" + IntegerMatch.dynamicPart + ")%.*?", Pattern.CASE_INSENSITIVE);
		private final String insertionCharacter = "\u001D";
		private final String message;
		private final List<IntegerMatch> matches = new ArrayList<IntegerMatch>();
		
		public DynamicMessage(String message)
		{
			Matcher matcher = dynamicPattern.matcher(message);
			while(matcher.matches())
			{
				IntegerMatch match = IntegerMatch.getNew(matcher.group(1));
				if(match != null)
				{
					message = message.replaceFirst(dynamicPattern.pattern().substring(2, dynamicPattern.pattern().length() - 2), insertionCharacter);
					matches.add(match);
					matcher = dynamicPattern.matcher(message);
				}
			}
			this.message = message;
		}
		
		public void sendMessage(TargetEventInfo eventInfo, Player player)
		{
			int currentCount = 0;
			String displayString = message;
			while(displayString.contains(insertionCharacter))
			{
				displayString = displayString.replaceFirst(insertionCharacter, matches.get(currentCount).getValue(eventInfo) + "");//FIXME Does this work?
				currentCount++;
			}
			player.sendMessage(displayString);
		}
	}
}