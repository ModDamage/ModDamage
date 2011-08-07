package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class Message extends Chanceroutine 
{
	protected final String playerName;
	protected final List<String> messages;
	public Message(String playerName, List<String> message)
	{
		this.playerName = playerName;
		this.messages = message;
	}
	@Override
	public void run(TargetEventInfo eventInfo)
	{ 
		Player player = TargetEventInfo.server.getPlayer(playerName);
		if(player != null)
			for(String message : messages)
				player.sendMessage(message);
	}
	
	public static void register(ModDamage routineUtility)
	{
		routineUtility.registerBase(Message.class, Pattern.compile("debug\\.([a-z0-9]+)\\.(_[a-z0-9]+)", Pattern.CASE_INSENSITIVE));//"debug\\.(server|(?:world(\\.[a-z0-9]+))|(?:player\\.[a-z0-9]+))\\.(_[a-z0-9]+)"
	}
	
	public static Message getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<String> matchedMessage = ModDamage.matchMessageAlias(matcher.group(2));
			if(!matchedMessage.isEmpty())
				return new Message(matcher.group(1), matchedMessage);
		}
		return null;
	}
}