package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class Message extends Chanceroutine 
{
	protected final String playerName;
	protected final String message;
	public Message(String playerName, String message)
	{
		this.playerName = playerName;
		this.message = message;
	}
	@Override
	public void run(TargetEventInfo eventInfo)
	{ 
		Player player = TargetEventInfo.server.getPlayer(playerName);
		if(player != null)
			player.sendMessage(message);	
	}
	
	public static Message getNew(Matcher matcher)
	{
		if(matcher != null)
			return new Message(matcher.group(1), matcher.group(2));
		return null;
	}
	
	public static void register(ModDamage routineUtility)
	{
		routineUtility.registerBase(Message.class, Pattern.compile("debug\\.(" + ModDamage.alphanumericPart + ")\\.(.+)", Pattern.CASE_INSENSITIVE));
	}
}