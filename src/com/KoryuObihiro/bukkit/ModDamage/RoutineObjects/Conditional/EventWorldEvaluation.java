package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class EventWorldEvaluation extends ConditionalStatement
{
	protected final HashSet<String> worlds;
	public EventWorldEvaluation(boolean inverted, HashSet<String> worlds)
	{
		super(inverted);
		this.worlds = worlds;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return worlds.contains(eventInfo.world.getName());}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EventWorldEvaluation.class, Pattern.compile("(!?)event\\.world\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EventWorldEvaluation getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			HashSet<String> worlds = ModDamage.matchWorldAlias(matcher.group(2));
			if(!worlds.isEmpty())
				return new EventWorldEvaluation(matcher.group(1).equalsIgnoreCase("!"), worlds);
		}
		return null;
	}
}
