package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityUnderwater extends EntityConditionalStatement<Material[]>
{
	public EntityUnderwater(boolean inverted, boolean forAttacker)
	{ 
		super(inverted, forAttacker, null);
	}
	@Override 
	protected boolean condition(TargetEventInfo eventInfo)
	{
		for(Material material : getRelevantInfo(eventInfo))
			if(!material.equals(Material.WATER) && !material.equals(Material.STATIONARY_WATER))
				return false;
		return true;
	}
	@Override
	protected Material[] getRelevantInfo(TargetEventInfo eventInfo)
	{
		LivingEntity entity = eventInfo.getRelevantEntity(forAttacker);
		
		Material[] entityBlocks = { Material.AIR, Material.AIR };
		if(entity != null)
		{
			entityBlocks[0] = entity.getLocation().getBlock().getType();
			entityBlocks[1] = entity.getEyeLocation().getBlock().getType();
		}
		return entityBlocks;
	}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityUnderwater.class, Pattern.compile("(!?)(\\w+)\\.underwater", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityUnderwater getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			return new EntityUnderwater(matcher.group(1).equalsIgnoreCase("!"), (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false);
		}
		return null;
	}
}
