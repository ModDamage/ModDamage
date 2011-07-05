package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity;

import java.util.List;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityWielding extends EntityConditionalCalculation 
{
	final Material material;
	public EntityWielding(boolean inverted, boolean forAttacker, Material material, List<ModDamageCalculation> calculations)
	{  
		this.inverted = inverted;
		this.forAttacker = forAttacker;
		this.material = material;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo)
	{ 
		return ((forAttacker?eventInfo.materialInHand_attacker:eventInfo.materialInHand_target) != null)
				?(forAttacker?eventInfo.materialInHand_attacker:eventInfo.materialInHand_target).equals(material)
				:false;
	}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return false;}
}
