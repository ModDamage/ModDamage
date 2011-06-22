package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;
import net.minecraft.server.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityUnderwater extends EntityConditionalCalculation 
{
	public EntityUnderwater(boolean forAttacker, List<DamageCalculation> calculations)
	{ 
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public int calculate(EventInfo eventInfo, int eventDamage) 
	{
		if((forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().add(0, 1, 0).getBlock().getType().equals(Material.WATER)
				&& (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlock().getType().equals(Material.WATER))
			return makeCalculations(eventInfo, eventDamage);
		return 0;
	}
}
