package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity;

import java.util.List;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityOnBlock extends EntityConditionalCalculation 
{
	final Material material;
	public EntityOnBlock(boolean inverted, boolean forAttacker, Material material, List<ModDamageCalculation> calculations)
	{ 
		this.inverted = inverted;
		this.material = material;
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().add(0, -1, 0).getBlock().getType().equals(material);}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return eventInfo.entity.getLocation().add(0, -1, 0).getBlock().getType().equals(material);}
}
