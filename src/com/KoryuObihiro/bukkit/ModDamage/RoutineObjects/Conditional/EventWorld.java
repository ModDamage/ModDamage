package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class EventWorld extends ConditionalStatement
{
	protected final List<String> worlds;
	public EventWorld(boolean inverted, List<String> worlds)
	{ 
		super(inverted);
		this.worlds = worlds;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return worlds.contains(eventInfo.world.getName());}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EventWorld.class, Pattern.compile("(!)?world\\.(" + ModDamage.potentialAliasPart + ")", Pattern.CASE_INSENSITIVE));
	}
	
	public static EventWorld getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<String> worlds = ModDamage.matchWorldAlias(matcher.group(2));
			if(!worlds.isEmpty())
				return new EventWorld(matcher.group(1) != null, worlds);
		}
		return null;
	}
}
