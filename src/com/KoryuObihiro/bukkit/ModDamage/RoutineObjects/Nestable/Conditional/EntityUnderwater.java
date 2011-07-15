package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;

public class EntityUnderwater extends EntityConditionalStatement<Material[]>
{
	static Material[] materialSet = { Material.WATER, Material.WATER };
	public EntityUnderwater(boolean inverted, boolean forAttacker)
	{ 
		super(forAttacker, forAttacker, materialSet);
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
		ConditionalRoutine.registerStatement(EntityUnderwater.class, Pattern.compile(RoutineUtility.entityPart + "underwater", Pattern.CASE_INSENSITIVE));
	}
}
