package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;


import org.bukkit.entity.Creature;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;

public class EntitySetAirTicks extends EntityDamageEffectCalculation 
{
	final int ticks;
	public EntitySetAirTicks(boolean forAttacker, int ticks)
	{
		this.forAttacker = forAttacker;
		this.ticks = ticks;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo){ ((Creature)(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target)).setRemainingAir(ticks);}
}
