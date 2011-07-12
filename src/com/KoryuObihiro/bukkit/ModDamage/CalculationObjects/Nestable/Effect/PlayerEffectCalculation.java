	package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

abstract public class PlayerEffectCalculation extends EffectCalculation<Player>
{
	protected final boolean forAttacker;
	public PlayerEffectCalculation(boolean forAttacker, List<ModDamageCalculation> calculations)
	{
		super(calculations);
		this.forAttacker = forAttacker;
	}

	@Override
	protected Player getAffectedObject(DamageEventInfo eventInfo){ return ((forAttacker?eventInfo.name_attacker:eventInfo.name_target) != null)?((Player)(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target)):null;}
	@Override
	protected Player getAffectedObject(SpawnEventInfo eventInfo){ return (eventInfo.entity instanceof Player)?((Player)eventInfo.entity):null;}
}
