package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class ServerOnlineMode extends ServerConditionalCalculation<Boolean>
{
	public ServerOnlineMode(boolean inverted, List<ModDamageCalculation> calculations)
	{
		super(inverted, true, calculations);
	}

	@Override
	protected boolean condition(DamageEventInfo eventInfo){ return (inverted?!server.getOnlineMode():server.getOnlineMode());}
	@Override
	protected boolean condition(SpawnEventInfo eventInfo){ return (inverted?!server.getOnlineMode():server.getOnlineMode());}
	
	public static void register()
	{
		CalculationUtility.register(ServerOnlineMode.class, Pattern.compile(CalculationUtility.ifPart + "server\\.hasOnlineMode", Pattern.CASE_INSENSITIVE));
	}
}
