package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.World;

import java.util.List;

import org.bukkit.World.Environment;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculation;

public class WorldEnvironment extends WorldConditionalSpawnCalculation 
{
	final Environment environment;
	public WorldEnvironment(Environment environment, List<SpawnCalculation> calculations)
	{
		this.environment = environment;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return eventInfo.world.getEnvironment().equals(environment);}
}
