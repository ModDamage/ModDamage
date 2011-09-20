package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ProjectileEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class EventHasRangedElement extends ConditionalStatement 
{
	protected EventHasRangedElement(boolean inverted)
	{
		super(inverted);
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo){ return eventInfo instanceof ProjectileEventInfo && ((ProjectileEventInfo)eventInfo).rangedElement != null;}
	
	public static void register()
	{
		//FIXME TESTING Furthest point of integration right now.
		ConditionalRoutine.registerStatement(EventHasRangedElement.class, Pattern.compile("(!?)event\\.hasrangedelement", Pattern.CASE_INSENSITIVE));
	}
	
	public static EventHasRangedElement getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			return new EventHasRangedElement(matcher.group(1).equalsIgnoreCase("!"));
		}
		return null;
	}
}
