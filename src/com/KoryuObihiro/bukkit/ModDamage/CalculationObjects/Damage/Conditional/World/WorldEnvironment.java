package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.World;

import java.util.List;

import org.bukkit.World.Environment;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class WorldEnvironment extends WorldConditionalDamageCalculation 
{
	final Environment environment;
	public WorldEnvironment(Environment environment, List<DamageCalculation> calculations)
	{
		this.environment = environment;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return eventInfo.world.getEnvironment().equals(environment);}
}
