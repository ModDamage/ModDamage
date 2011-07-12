package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityUnderwater extends EntityConditionalCalculation<Material[]>
{
	static Material[] materialSet = { Material.WATER, Material.WATER };
	public EntityUnderwater(boolean inverted, boolean forAttacker, List<ModDamageCalculation> calculations)
	{ 
		super(forAttacker, forAttacker, materialSet, calculations);
	}
	@Override
	protected Material[] getRelevantInfo(DamageEventInfo eventInfo)
	{
		Material[] entityBlocks = { getRelevantEntity(eventInfo).getLocation().getBlock().getType(), getRelevantEntity(eventInfo).getEyeLocation().getBlock().getType() };
		return entityBlocks;
	}
	@Override
	protected Material[] getRelevantInfo(SpawnEventInfo eventInfo)
	{
		Material[] entityBlocks = { getRelevantEntity(eventInfo).getLocation().getBlock().getType(), getRelevantEntity(eventInfo).getEyeLocation().getBlock().getType() };
		return entityBlocks;
	}
	
	public static void register()
	{
		CalculationUtility.register(EntityUnderwater.class, Pattern.compile(CalculationUtility.ifPart + CalculationUtility.entityPart + "underwater", Pattern.CASE_INSENSITIVE));
	}
}
