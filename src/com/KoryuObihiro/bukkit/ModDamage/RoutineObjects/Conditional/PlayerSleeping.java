package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class PlayerSleeping extends EntityConditionalStatement<Boolean>
{
	public PlayerSleeping(boolean inverted, boolean forAttacker)
	{  
		super(inverted, forAttacker, true);
	}

	@Override
	protected Boolean getRelevantInfo(TargetEventInfo eventInfo){ return eventInfo.getRelevantEntity(forAttacker) instanceof Player && ((Player)eventInfo.getRelevantEntity(forAttacker)).isSleeping();}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, PlayerSleeping.class, Pattern.compile("(!)?" + ModDamage.entityRegex + "\\.sleeping", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerSleeping getNew(Matcher matcher)
	{
		if(matcher != null)
			return new PlayerSleeping(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"));
		return null;
	}
}
