package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityOnBlock extends EntityConditionalCalculation<Material>
{
	final Material material;
	public EntityOnBlock(boolean inverted, boolean forAttacker, Material material, List<ModDamageCalculation> calculations)
	{ 
		super(inverted, forAttacker, material, calculations);
		this.material = material;
	}
	@Override
	protected Material getRelevantInfo(DamageEventInfo eventInfo){ return getRelevantEntity(eventInfo).getLocation().add(0, -1, 0).getBlock().getType();}
	@Override
	protected Material getRelevantInfo(SpawnEventInfo eventInfo){ return getRelevantEntity(eventInfo).getLocation().add(0, -1, 0).getBlock().getType();}
}
