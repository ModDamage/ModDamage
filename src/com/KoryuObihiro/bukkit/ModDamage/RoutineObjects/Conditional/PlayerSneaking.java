package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class PlayerSneaking extends EntityConditionalStatement<Boolean>
{
	public PlayerSneaking(boolean inverted, boolean forAttacker)
	{  
		super(inverted, forAttacker, true);
	}

	@Override
	protected Boolean getRelevantInfo(TargetEventInfo eventInfo){ return eventInfo.getRelevantEntity(forAttacker) instanceof Player && ((Player)eventInfo.getRelevantEntity(forAttacker)).isSneaking();}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, PlayerSneaking.class, Pattern.compile("(!)?(\\w+)\\.sneaking", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerSneaking getNew(Matcher matcher)
	{
		if(matcher != null)
			return new PlayerSneaking(matcher.group(1) != null, (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false);
		return null;
	}
}
