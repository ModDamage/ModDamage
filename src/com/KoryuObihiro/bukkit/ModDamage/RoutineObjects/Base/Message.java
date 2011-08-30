package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;

public class Message extends Chanceroutine 
{
	protected final boolean forAttacker;
	protected final List<String> messages;
	protected final MessageType messageType;
	public Message(String configString, boolean forAttacker, List<String> message)
	{
		super(configString);
		this.messageType = MessageType.ENTITY;
		this.forAttacker = forAttacker;
		this.messages = message;
	}
	public Message(String configString, MessageType messageType, List<String> message)
	{
		super(configString);
		this.messageType = messageType;
		this.forAttacker = false;
		this.messages = message;
	}
	@Override
	public void run(TargetEventInfo eventInfo)
	{ 
		messageType.sendMessage(forAttacker, eventInfo, messages);
	}
	
	public static void register(ModDamage routineUtility)
	{
		routineUtility.registerBase(Message.class, Pattern.compile("message\\.(\\w+)\\.(.*)", Pattern.CASE_INSENSITIVE));//"debug\\.(server|(?:world(\\.[a-z0-9]+))|(?:player\\.[a-z0-9]+))\\.(_[a-z0-9]+)"
	}
	
	public static Message getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<String> matchedMessage = ModDamage.matchMessageAlias(matcher.group(2));
			if(!matchedMessage.isEmpty())
			{
				String key = matcher.group(1);
				if(key.equalsIgnoreCase("target") || key.equalsIgnoreCase("attacker"))
					return new Message(matcher.group(), key.equalsIgnoreCase("attacker"), matchedMessage);
				else if(MessageType.match(key) != null)
					return new Message(matcher.group(), MessageType.match(key), matchedMessage);
				ModDamage.addToConfig(DebugSetting.QUIET, 0, "Unrecognized message recipient \"" + key + "\"", LoadState.FAILURE);
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
				if(key.equalsIgnoreCase(messageType.name()))
					return messageType;
			return null;
		}
		
		private void sendMessage(boolean forAttacker, TargetEventInfo eventInfo, List<String> messages)
		{
			switch(this)
			{
				case ENTITY:
					if(eventInfo.getRelevantEntity(forAttacker) instanceof Player)
					{
						Player player = (Player)eventInfo.getRelevantEntity(forAttacker);
						for(String message : messages)
							player.sendMessage(message);
					}
					break;
				case WORLD:
					for(Player player : eventInfo.world.getPlayers())
						for(String message : messages)
						{
							player.sendMessage(message);
						}
					break;
				case SERVER:
					for(Player player : TargetEventInfo.server.getOnlinePlayers())
						for(String message : messages)
						{
							player.sendMessage(message);
						}
					break;
			}
		}
	}
}