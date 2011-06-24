package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityAltitudeLessThanEquals extends EntityDamageConditionalCalculation 
{
	final int altitude;
	public EntityAltitudeLessThanEquals(int altitude, boolean forAttacker, List<DamageCalculation> calculations)
	{ 
		this.altitude = altitude;
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlockY() <= altitude;}
}
