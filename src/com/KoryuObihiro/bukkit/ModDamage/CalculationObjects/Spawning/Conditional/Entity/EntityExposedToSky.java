package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity;

import java.util.List;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityExposedToSky extends EntityConditionalCalculation 
{
	public EntityExposedToSky(boolean forAttacker, List<DamageCalculation> calculations)
	{ 
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo)
	{ 
		int i = (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlockX();
		int k = (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlockZ();
		for(int j = (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlockY(); j < 128; j++)
			//FIXME Add more block types!...might be expensive though.
			if(!eventInfo.world.getBlockAt(i, j, k).equals(Material.AIR))
				return false;
		return true;
	}
}
