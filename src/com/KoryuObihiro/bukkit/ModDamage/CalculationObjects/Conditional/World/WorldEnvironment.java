package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.World;

import java.util.List;

import org.bukkit.World.Environment;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class WorldEnvironment extends WorldConditionalCalculation 
{
	final Environment environment;
	public WorldEnvironment(boolean inverted, Environment environment, List<ModDamageCalculation> calculations)
	{
		this.inverted = inverted;
		this.environment = environment;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return eventInfo.world.getEnvironment().equals(environment);}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return eventInfo.world.getEnvironment().equals(environment);}
}
