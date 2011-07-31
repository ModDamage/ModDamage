package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public class ServerOnlineMode extends ServerConditionalStatement<Boolean>
{
	public ServerOnlineMode(boolean inverted)
	{
		super(inverted, true);
	}

	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (inverted?!server.getOnlineMode():server.getOnlineMode());}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return (inverted?!server.getOnlineMode():server.getOnlineMode());}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, ServerOnlineMode.class, Pattern.compile("(!)?server\\.onlineModeEnabled", Pattern.CASE_INSENSITIVE));
	}
	
	public static ServerOnlineMode getNew(Matcher matcher)
	{
		if(matcher != null)
			return new ServerOnlineMode(matcher.group(1) != null);
		return null;
	}
}
