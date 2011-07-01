package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity;

import java.util.List;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityBiome extends EntityConditionaDamageCalculation 
{
	final Biome biome;
	public EntityBiome(boolean inverted, boolean forAttacker, Biome biome, List<DamageCalculation> calculations)
	{ 
		this.inverted = inverted;
		this.biome = biome;
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlock().getBiome().equals(biome);}
}
