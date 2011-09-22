package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Message.DynamicMessage;

public class Message extends Chanceroutine 
{
	//FIXME Need to implement a special class that contains references and does .toString(), for dynamic referencing. Also, need color.
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
	public Message(String configString, MessageType messageType, List<DynamicMessage> message)
	{
		super(configString);
		this.messageType = messageType;
		this.entityReference = null;
		this.messages = message;
	}
	@Override
	public void run(TargetEventInfo eventInfo)
	{ 
		messageType.sendMessage(entityReference, eventInfo, messages);
	}
	
	public static void register()
	{
		Routine.registerBase(Message.class, Pattern.compile("message\\.(\\w+)\\.(.*)", Pattern.CASE_INSENSITIVE));//"debug\\.(server|(?:world(\\.[a-z0-9]+))|(?:player\\.[a-z0-9]+))\\.(_[a-z0-9]+)"
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
}