package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity;

import java.util.List;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityWielding extends EntityConditionaDamageCalculation 
{
	final Material material;
	public EntityWielding(boolean inverted, boolean forAttacker, Material material, List<DamageCalculation> calculations)
	{  
		this.inverted = inverted;
		this.forAttacker = forAttacker;
		this.material = material;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.materialInHand_attacker:eventInfo.materialInHand_target).equals(material);}
	//TODO Check that this is the correct integer for falling.
}
