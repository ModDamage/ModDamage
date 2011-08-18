package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class ServerOnlineMode extends ServerConditionalStatement<Boolean>
{
	public ServerOnlineMode(boolean inverted)
	{
		super(inverted, true);
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return server.getOnlineMode();}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, ServerOnlineMode.class, Pattern.compile("(!?)server\\.onlineMode", Pattern.CASE_INSENSITIVE));
	}
	
	public static ServerOnlineMode getNew(Matcher matcher)
	{
		if(matcher != null)
			return new ServerOnlineMode(matcher.group(1).equalsIgnoreCase("!"));
		return null;
	}
}
