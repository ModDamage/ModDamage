package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class PlayerSneaking extends PlayerConditionalStatement
{
	public PlayerSneaking(boolean inverted, EntityReference entityReference)
	{  
		super(inverted, entityReference);
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{ 
		Player player = getRelevantPlayer(eventInfo);
		return (player != null && player.isSneaking());
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(PlayerSneaking.class, Pattern.compile("(!?)(\\w+)\\.sneaking", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerSneaking getNew(Matcher matcher)
	{
		if(matcher != null)
			if(EntityReference.isValid(matcher.group(2)))
				return new PlayerSneaking(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)));
		return null;
	}
}
