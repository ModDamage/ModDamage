package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;

public class Message extends Chanceroutine 
{
	public Message(){}
	@Override
	public void run(DamageEventInfo eventInfo)
	{ 
		Player koryu = eventInfo.server.getPlayer("KoryuObihiro");
		if(koryu != null)
			koryu.sendMessage("bump");
			
	}
	@Override
	public void run(SpawnEventInfo eventInfo){ eventInfo.eventHealth = Math.abs(random.nextInt()%(eventInfo.eventHealth + 1));}
	
	public static Message getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new Message();
		return null;
	}
	
	public static void register(RoutineUtility routineUtility)
	{
		routineUtility.registerBase(Message.class, Pattern.compile("debug", Pattern.CASE_INSENSITIVE));
	}
}