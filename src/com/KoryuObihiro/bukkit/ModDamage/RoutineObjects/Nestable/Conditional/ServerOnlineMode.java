package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;

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
	
	public static void register(RoutineUtility routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, ServerOnlineMode.class, Pattern.compile("server\\.hasOnlineMode", Pattern.CASE_INSENSITIVE));
	}
}
