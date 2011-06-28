package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity;

import java.util.List;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityOnBlock extends EntityConditionaDamageCalculation 
{
	final Material material;
	public EntityOnBlock(boolean forAttacker, boolean inverted, Material material, List<DamageCalculation> calculations)
	{ 
		this.inverted = inverted;
		this.material = material;
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().add(0, -1, 0).getBlock().getType().equals(material);}
}
