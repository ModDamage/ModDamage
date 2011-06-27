package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;


import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;

public class EntityExplode extends EntityEffectDamageCalculation 
{
	final int power;
	public EntityExplode(boolean forAttacker, int power)
	{
		this.forAttacker = forAttacker;
		this.power = power;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.world.createExplosion((forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation(), power);}
}
