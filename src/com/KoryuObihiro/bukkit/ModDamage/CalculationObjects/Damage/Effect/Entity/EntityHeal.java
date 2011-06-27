package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;


import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;

public class EntityHeal extends EntityEffectDamageCalculation 
{
	public EntityHeal(boolean forAttacker)
	{
		this.forAttacker = forAttacker;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{ 
		eventInfo.entity_attacker.setHealth(eventInfo.entity_attacker.getHealth() + eventInfo.eventDamage);
		eventInfo.eventDamage = 0;
	}
}
