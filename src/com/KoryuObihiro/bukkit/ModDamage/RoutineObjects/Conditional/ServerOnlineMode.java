package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class ServerOnlineMode extends ConditionalStatement
{
	public ServerOnlineMode(boolean inverted)
	{
		super(inverted);
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return Bukkit.getOnlineMode();}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(ServerOnlineMode.class, Pattern.compile("(!?)server\\.onlineMode", Pattern.CASE_INSENSITIVE));
	}
	
	public static ServerOnlineMode getNew(Matcher matcher)
	{
		if(matcher != null)
			return new ServerOnlineMode(matcher.group(1).equalsIgnoreCase("!"));
		return null;
	}
}
