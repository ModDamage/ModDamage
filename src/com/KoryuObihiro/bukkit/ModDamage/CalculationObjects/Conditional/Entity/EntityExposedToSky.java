package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityExposedToSky extends EntityConditionalCalculation 
{
	public EntityExposedToSky(boolean inverted, boolean forAttacker, List<ModDamageCalculation> calculations)
	{  
		this.inverted = inverted;
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return isExposedToSky(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target, eventInfo.world);}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return isExposedToSky(eventInfo.entity, eventInfo.world);}
	private boolean isExposedToSky(LivingEntity entity, World world)
	{
		int i = entity.getLocation().getBlockX();
		int k = entity.getLocation().getBlockZ();
		for(int j = entity.getLocation().getBlockY(); j < 128; j++)
			//FIXME Add more block types!...might be expensive though.
			if(!world.getBlockAt(i, j, k).equals(Material.AIR))
				return false;
		return true;
	}

}
