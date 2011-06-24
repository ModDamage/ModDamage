package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;


import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;

public class EntitySetFireTicks extends EntityDamageEffectCalculation 
{
	final int ticks;
	public EntitySetFireTicks(boolean forAttacker, int ticks)
	{
		this.forAttacker = forAttacker;
		this.ticks = ticks;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo){ (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).setFireTicks(ticks);}
}
