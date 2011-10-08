package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

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

public class Message extends NestedRoutine 
{
	protected static final Pattern nestedMessagePattern = Pattern.compile("message.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	//FIXME Need colors!
	protected final EntityReference entityReference;
	protected final List<DynamicMessage> messages;
	protected final MessageType messageType;
	public Message(String configString, EntityReference entityReference, List<DynamicMessage> messages)
	{
		super(configString);
		this.messageType = MessageType.ENTITY;
		this.entityReference = entityReference;
		this.messages = messages;
	}
	public Message(String configString, MessageType messageType, List<DynamicMessage> messages)
	{
		super(configString);
		this.messageType = messageType;
		this.entityReference = null;
		this.messages = messages;
	}
	@Override
	public void run(TargetEventInfo eventInfo)
	{ 
		messageType.sendMessage(entityReference, eventInfo, messages);
	}
	
	public static void register()
	{
		Routine.registerBase(Message.class, Pattern.compile("message\\.(\\w+)\\.(.*)", Pattern.CASE_INSENSITIVE));//"debug\\.(server|(?:world(\\.[a-z0-9]+))|(?:player\\.[a-z0-9]+))\\.(_[a-z0-9]+)"
		NestedRoutine.registerNested(Message.class, nestedMessagePattern);
	}
	
	public static Message getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<DynamicMessage> messages = ModDamage.matchMessageAlias(matcher.group(2));
			if(!messages.isEmpty())
			{
				Message routine = null;
				if(EntityReference.isValid(matcher.group(1)))
					routine = new Message(matcher.group(), EntityReference.match(matcher.group(1)), messages);
				else if(MessageType.match(matcher.group(1)) != null)
					routine = new Message(matcher.group(), MessageType.match(matcher.group(1)), messages);
				if(routine != null) return routine;
			}
			else ModDamage.addToLogRecord(DebugSetting.NORMAL, "Message content \"" + matcher.group(3) + "\" is invalid.", LoadState.SUCCESS);
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
				if(!failFlag)
				{
					ModDamage.addToLogRecord(DebugSetting.NORMAL, "Message: \"" + string + "\"" , LoadState.SUCCESS);
					ModDamage.indentation++;
					for(DynamicMessage message : routine.messages)
						ModDamage.addToLogRecord(DebugSetting.NORMAL, "- \"" + message.toString() + "\"" , LoadState.SUCCESS);
					ModDamage.indentation--;
					return routine;
				}
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
			ModDamage.addToLogRecord(DebugSetting.QUIET, "Unrecognized message recipient \"" + key + "\"", LoadState.FAILURE);
			return null;
		}
		
		protected void sendMessage(EntityReference entityReference, TargetEventInfo eventInfo, List<DynamicMessage> messages)
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
		private static final Pattern integerReplacePattern = Pattern.compile("(.*)%" + IntegerMatch.dynamicIntegerPart + "%(.*)", Pattern.CASE_INSENSITIVE);
		private static final Pattern colorReplacePattern = Pattern.compile("(.*)&([0-9a-f])(.*)", Pattern.CASE_INSENSITIVE);
		
		private final String insertionCharacter = "\u001D";
		private final String message;
		private final List<IntegerMatch> matches = new ArrayList<IntegerMatch>();
		
		public DynamicMessage(String message)
		{
			Matcher integerMatcher = integerReplacePattern.matcher(message);
			while(integerMatcher.matches())
			{
				IntegerMatch match = IntegerMatch.getNew(integerMatcher.group(2));
				if(match != null)
				{
					message = integerMatcher.group(1) + insertionCharacter + integerMatcher.group(3);
					matches.add(match);
					integerMatcher = integerReplacePattern.matcher(message);
				}
				else
				{
					message = integerMatcher.group(1) + "INVALID" + integerMatcher.group(3);
					integerMatcher = integerReplacePattern.matcher(message);
				}
			}
			Matcher colorMatcher = colorReplacePattern.matcher(message);
			while(colorMatcher.matches())
			{
				message = colorMatcher.group(1) + String.format("\u00A7%s", colorMatcher.group(2)) + colorMatcher.group(3);
				colorMatcher = colorReplacePattern.matcher(message);
			}
			this.message = message;
		}
		
		public void sendMessage(TargetEventInfo eventInfo, Player player)
		{
			int currentCount = matches.size() - 1;
			String displayString = message;
			while(displayString.contains(insertionCharacter))
			{
				displayString = displayString.replaceFirst(insertionCharacter, matches.get(currentCount).getValue(eventInfo) + "");
				currentCount--;
			}
			player.sendMessage(displayString);
		}
		
		@Override
		public String toString()//TODO Algorithm is retarded, but not sure how else to do it at this point.
		{
			int currentCount = matches.size() - 1;
			String displayString = message;
			while(displayString.contains(insertionCharacter))
			{
				displayString = displayString.replaceFirst(insertionCharacter, "%" + matches.get(currentCount).toString() + "%");
				currentCount--;
			}
			return displayString;
		}
	}
}