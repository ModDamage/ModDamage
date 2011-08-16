package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class EventWorldEvaluation extends ConditionalStatement
{
	protected final List<String> worlds;
	public EventWorldEvaluation(boolean inverted, List<String> worlds)
	{ 
		super(inverted);
		this.worlds = worlds;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return worlds.contains(eventInfo.world.getName());}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EventWorldEvaluation.class, Pattern.compile("(!)?world\\.(" + ModDamage.aliasPart + ")", Pattern.CASE_INSENSITIVE));
	}
	
	public static EventWorldEvaluation getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<String> worlds = ModDamage.matchWorldAlias(matcher.group(2));
			if(!worlds.isEmpty())
				return new EventWorldEvaluation(matcher.group(1) != null, worlds);
		}
		return null;
	}
}
