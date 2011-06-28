package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;


import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntitySetItem extends EntityEffectDamageCalculation 
{
	final Material material;
	int quantity;
	public EntitySetItem(boolean forAttacker, Material material, List<DamageCalculation> calculations)
	{
		this.forAttacker = forAttacker;
		this.material = material;
		this.calculations = calculations;
	}
	public EntitySetItem(boolean forAttacker, Material material, int quantity)
	{
		this.forAttacker = forAttacker;
		this.material = material;
		this.quantity = quantity;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{ 
		if(calculations != null)
		{
			quantity = eventInfo.eventDamage;
			for(DamageCalculation calculation : calculations)
				calculation.calculate(eventInfo);
			((Player)(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target)).setItemInHand(new ItemStack(material, eventInfo.eventDamage));
			eventInfo.eventDamage = quantity;
		}
		else ((Player)(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target)).setItemInHand(new ItemStack(material, quantity));
	}
}
