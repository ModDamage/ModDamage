package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityLightLevelNotEquals extends EntityConditionaDamageCalculation 
{
	final byte lightLevel;
	public EntityLightLevelNotEquals(boolean inverted, boolean forAttacker, byte lightLevel, List<DamageCalculation> calculations)
	{ 
		this.inverted = inverted;
		this.lightLevel = lightLevel;
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlock().getLightLevel() != lightLevel;}
}
