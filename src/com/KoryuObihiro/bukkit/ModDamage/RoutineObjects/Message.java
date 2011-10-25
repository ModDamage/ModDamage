package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicString;

public class Message extends NestedRoutine 
{
	protected static final Pattern nestedMessagePattern = Pattern.compile("message.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	protected final EntityReference entityReference;
	protected final List<DynamicMessage> messages;
	protected final MessageType messageType;
	
	private Message(String configString, EntityReference entityReference, List<DynamicMessage> messages)
	{
		super(configString);
		this.messageType = MessageType.ENTITY;
		this.entityReference = entityReference;
		this.messages = messages;
	}
	private Message(String configString, MessageType messageType, List<DynamicMessage> messages)
	{
		super(configString);
		this.messageType = messageType;
		this.entityReference = null;
		this.messages = messages;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		switch(messageType)
		{
			case ENTITY:
				if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
					DynamicMessage.sendMessages(messages, eventInfo, (Player)entityReference.getEntity(eventInfo));
				break;
			case WORLD:
				for(Player player : eventInfo.world.getPlayers())
					DynamicMessage.sendMessages(messages, eventInfo, player);
				break;
			case SERVER:
				for(Player player : Bukkit.getOnlinePlayers())
					DynamicMessage.sendMessages(messages, eventInfo, player);
				break;
		}
	}
	
	private enum MessageType
	{
		ENTITY, WORLD, SERVER;
		
		protected static MessageType match(String key)
		{
			for(MessageType messageType : MessageType.values())
			{
				if(messageType.equals(MessageType.ENTITY)) continue;
				if(key.equalsIgnoreCase(messageType.name()))
					return messageType;
			}
			if(EntityReference.isValid(key))
				return ENTITY;
			return null;
		}
	}
	
	public static class DynamicMessage
	{
		private static final Pattern stringReplacePattern = Pattern.compile("(.*)%([^%]+)%(.*)", Pattern.CASE_INSENSITIVE);
		private static final Pattern colorReplacePattern = Pattern.compile("(.*)&([0-9a-f])(.*)", Pattern.CASE_INSENSITIVE);
		
		private final String insertionCharacter = "\u001D";
		private final String message;
		private final List<DynamicString> matches = new ArrayList<DynamicString>();
		
		public DynamicMessage(String message)
		{
			Matcher integerMatcher = stringReplacePattern.matcher(message);
			while(integerMatcher.matches())
			{
				ModDamage.addToLogRecord(DebugSetting.VERBOSE, "Matched dynamic integer " + integerMatcher.group(2) + ", attempting to get reference...", LoadState.SUCCESS);
				DynamicString match = DynamicString.getNew(integerMatcher.group(2));
				if(match != null)
				{
					ModDamage.addToLogRecord(DebugSetting.VERBOSE, "Reference found: \"" + match.toString() + "\"", LoadState.SUCCESS);
					message = integerMatcher.group(1) + insertionCharacter + integerMatcher.group(3);
					matches.add(match);
					integerMatcher = stringReplacePattern.matcher(message);
				}
				else
				{
					ModDamage.addToLogRecord(DebugSetting.VERBOSE, "Reference not found, marking invalid.", LoadState.SUCCESS);
					message = integerMatcher.group(1) + "INVALID" + integerMatcher.group(3);
					integerMatcher = stringReplacePattern.matcher(message);
				}
			}
			Matcher colorMatcher = colorReplacePattern.matcher(message);
			while(colorMatcher.matches())
			{
				ModDamage.addToLogRecord(DebugSetting.VERBOSE, "Matched color " + colorMatcher.group(2) + "", LoadState.SUCCESS);
				message = colorMatcher.group(1) + String.format("\u00A7%s", colorMatcher.group(2)) + colorMatcher.group(3);
				colorMatcher = colorReplacePattern.matcher(message);
			}
			
			ModDamage.addToLogRecord(DebugSetting.VERBOSE, "Resulting Message string: \"" + message + "\"", LoadState.SUCCESS);
			this.message = message;
		}
		
		private void sendMessage(TargetEventInfo eventInfo, Player player)
		{
			int currentCount = matches.size() - 1;
			String displayString = message;
			while(displayString.contains(insertionCharacter))
			{
				displayString = displayString.replaceFirst(insertionCharacter, matches.get(currentCount).getString(eventInfo) + "");
				currentCount--;
			}
			player.sendMessage(displayString);
		}
		
		public static void sendMessages(List<DynamicMessage> messages, TargetEventInfo eventInfo, Player player)
		{
			for(DynamicMessage message : messages)
				message.sendMessage(eventInfo, player);
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
	
	public static void register()
	{
		Routine.registerBase(Message.class, Pattern.compile("message\\.(\\w+)\\.(.*)", Pattern.CASE_INSENSITIVE));//"debug\\.(server|(?:world(\\.[a-z0-9]+))|(?:player\\.[a-z0-9]+))\\.(_[a-z0-9]+)"
		NestedRoutine.registerNested(Message.class, nestedMessagePattern);
	}
	
	public static Message getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			MessageType messageType = MessageType.match(matcher.group(1));
			List<DynamicMessage> messages = ModDamage.matchMessageAlias(matcher.group(2));
			if(!messages.isEmpty() && messageType != null)
			{
				reportContents(messages);
				switch(messageType)
				{
					case ENTITY:
						return new Message(matcher.group(), EntityReference.match(matcher.group(1)), messages);
					case WORLD:
					case SERVER:
						return new Message(matcher.group(), messageType, messages);
				}
			}
			else ModDamage.addToLogRecord(DebugSetting.QUIET, "Message content \"" + matcher.group(3) + "\" is invalid.", LoadState.NOT_LOADED);
		}
		return null;
	}
	
	public static Message getNew(String string, Object nestedContent)
	{
		if(string != null && nestedContent != null)
		{
			String[] splitParts = string.split("\\.");
			MessageType messageType = MessageType.match(splitParts[1]);
			if(nestedContent instanceof List)
			{
				boolean failFlag = false;
				List<DynamicMessage> messages = new ArrayList<DynamicMessage>();
				for(Object object : (List<?>)nestedContent)
				{
					if(!(object instanceof String))
						failFlag = true;
					messages.addAll(ModDamage.matchMessageAlias((String)object));
				}
				
				Message routine = null;
				switch(messageType)
				{
					case ENTITY:
						routine = new Message(string, EntityReference.match(splitParts[1]), messages);
						break;
					case WORLD:
					case SERVER:
						routine = new Message(string, messageType, messages);
				}
				if(!failFlag)
				{
					reportContents(messages);
					return routine;
				}
			}	
		}
		return null;
	}
	private static void reportContents(List<DynamicMessage> messages)
	{
		ModDamage.addToLogRecord(DebugSetting.NORMAL, "Message: \"" + messages.get(0).toString() + "\"" , LoadState.SUCCESS);
		ModDamage.indentation++;
		for(int i = 1; i < messages.size(); i++)
			ModDamage.addToLogRecord(DebugSetting.NORMAL, "- \"" + messages.get(i).toString() + "\"" , LoadState.SUCCESS);
		ModDamage.indentation--;
	}
}