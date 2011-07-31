package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

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
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityUnderwater.class, Pattern.compile("(!)?" + ModDamage.entityPart + "underwater", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityUnderwater getNew(Matcher matcher)
	{
		if(matcher != null)
			return new EntityUnderwater(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"));
		return null;
	}
}
