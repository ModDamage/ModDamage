package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicString;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.NestedRoutine;

public class Message extends NestedRoutine 
{	
	protected final EntityReference entityReference;
	protected final Collection<DynamicMessage> messages;
	protected final MessageType messageType;
	
	private Message(String configString, EntityReference entityReference, Collection<DynamicMessage> messages)
	{
		super(configString);
		this.messageType = MessageType.ENTITY;
		this.entityReference = entityReference;
		this.messages = messages;
	}
	private Message(String configString, MessageType messageType, Collection<DynamicMessage> messages)
	{
		super(configString);
		this.messageType = messageType;
		this.entityReference = null;
		this.messages = messages;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		List<Player> players = null;
		switch(messageType)
		{
			case ENTITY:
				players = (entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER)?Arrays.asList((Player)entityReference.getEntity(eventInfo)):Arrays.<Player>asList());
				break;
			case WORLD:
				players = eventInfo.world.getPlayers();
				break;
			case SERVER:
				players = Arrays.asList(Bukkit.getOnlinePlayers());
				break;
		}
		
		if(!players.isEmpty())
		{
			List<String> strings = new ArrayList<String>();
			for(DynamicMessage message :  messages)
				strings.add(message.getMessage(eventInfo));
			for(Player player : players)
				for(String string : strings)
					player.sendMessage(string);
		}
	}
	
	private enum MessageType
	{
		ENTITY, WORLD, SERVER;
		protected static MessageType match(String key)
		{
			for(MessageType type : MessageType.values())
				if(type != MessageType.ENTITY && type.name().equalsIgnoreCase(key))
					return type;
			EntityReference reference = EntityReference.match(key);
			if(reference != null)
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
			ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
			Matcher integerMatcher = stringReplacePattern.matcher(message);
			while(integerMatcher.matches())
			{
				DynamicString match = DynamicString.getNew(integerMatcher.group(2));
				if(match != null)
				{
					ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Matched dynamic string: \"" + match.toString() + "\"");
					message = integerMatcher.group(1) + insertionCharacter + integerMatcher.group(3);
					matches.add(match);
					integerMatcher = stringReplacePattern.matcher(message);
				}
				else
				{
					ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, "Dynamic string not found, marking invalid.");
					message = integerMatcher.group(1) + "$INVALID$" + integerMatcher.group(3);
					integerMatcher = stringReplacePattern.matcher(message);
				}
			}
			Matcher colorMatcher = colorReplacePattern.matcher(message);
			while(colorMatcher.matches())
			{
				ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Matched color " + colorMatcher.group(2) + "");
				message = colorMatcher.group(1) + String.format("\u00A7%s", colorMatcher.group(2)) + colorMatcher.group(3);
				colorMatcher = colorReplacePattern.matcher(message);
			}
			
			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Resulting Message string: \"" + message + "\"");
			this.message = message;
		}
		
		public String getMessage(TargetEventInfo eventInfo)
		{
			int currentCount = matches.size() - 1;
			String displayString = message;
			while(displayString.contains(insertionCharacter))
			{
				displayString = displayString.replaceFirst(insertionCharacter, matches.get(currentCount).getString(eventInfo) + "");
				currentCount--;
			}
			return displayString;
		}
		
		@Override
		public String toString()//TODO Algorithm is retarded, but not sure how else to do it at this point.
		{
			String displayString = message;
			for(int i = matches.size() - 1; i >= 0; i--)
				displayString = displayString.replaceFirst(insertionCharacter, "%" + matches.get(i).toString() + "%");
			return displayString;
		}
	}
	
	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("message\\.(\\w+)\\.(.+)", Pattern.CASE_INSENSITIVE), new BaseRoutineBuilder());//"debug\\.(server|(?:world(\\.[a-z0-9]+))|(?:player\\.[a-z0-9]+))\\.(_[a-z0-9]+)"
		NestedRoutine.registerRoutine(Pattern.compile("message.(\\w+)", Pattern.CASE_INSENSITIVE), new NestedRoutineBuilder());
	}
	
	protected static class BaseRoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Message getNew(Matcher matcher)
		{
			if(matcher != null)
			{
				MessageType messageType = MessageType.match(matcher.group(1));
				Collection<DynamicMessage> messages = AliasManager.matchMessageAlias(matcher.group(2));
				if(!messages.isEmpty() && messageType != null)
				{
					Message routine = null;
					switch(messageType)
					{
						case ENTITY:
							routine = new Message(matcher.group(), EntityReference.match(matcher.group(1)), messages);
							break;
						case WORLD:
						case SERVER:
							routine = new Message(matcher.group(), messageType, messages);
					}
					routine.reportContents();
					return routine;
				}
				else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: Message content is invalid.");
			}
			return null;
		}
	}
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Message getNew(Matcher matcher, Object nestedContent)
		{
			if(matcher != null && nestedContent != null)
			{
				MessageType messageType = MessageType.match(matcher.group(1));
				if(nestedContent instanceof List)
				{
					boolean failFlag = false;
					List<DynamicMessage> messages = new ArrayList<DynamicMessage>();
					for(Object object : (List<?>)nestedContent)
					{
						if(!(object instanceof String))
							failFlag = true;
						messages.addAll(AliasManager.matchMessageAlias((String)object));
					}
					
					Message routine = null;
					switch(messageType)
					{
						case ENTITY:
							routine = new Message(matcher.group(), EntityReference.match(matcher.group(1)), messages);
							break;
						case WORLD:
						case SERVER:
							routine = new Message(matcher.group(), messageType, messages);
					}
					if(!failFlag)
					{
						routine.reportContents();
						return routine;
					}
				}	
			}
			return null;
		}
	}
	
	private void reportContents()
	{
		if(messages instanceof List)
		{
			String routineString = "Message (" + (entityReference != null?entityReference.toString():messageType.name()) + ")";
			List<DynamicMessage> messageList = (List<DynamicMessage>)messages;
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
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
	}
}