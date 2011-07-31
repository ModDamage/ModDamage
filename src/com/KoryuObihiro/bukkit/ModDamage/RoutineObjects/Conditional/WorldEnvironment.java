package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World.Environment;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class WorldEnvironment extends WorldConditionalStatement 
{
	protected final Environment environment;
	public WorldEnvironment(boolean inverted, Environment environment)
	{
		super(inverted);
		this.environment = environment;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (useEventWorld?eventInfo.world:world).getEnvironment().equals(environment);}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return (useEventWorld?eventInfo.world:world).getEnvironment().equals(environment);}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, WorldEnvironment.class, Pattern.compile("(!)?world\\.environment\\." + ModDamage.environmentRegex, Pattern.CASE_INSENSITIVE));
	}
	
	public static WorldEnvironment getNew(Matcher matcher)
	{
		if(matcher != null)
			return new WorldEnvironment(matcher.group(1) != null, ModDamage.matchEnvironment(matcher.group(3)));
		return null;
	}
}
