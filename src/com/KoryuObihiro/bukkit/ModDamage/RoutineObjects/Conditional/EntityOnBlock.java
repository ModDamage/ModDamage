package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public class EntityOnBlock extends EntityConditionalStatement<Material>
{
	final Material material;
	public EntityOnBlock(boolean inverted, boolean forAttacker, Material material)
	{ 
		super(inverted, forAttacker, material);
		this.material = material;
	}
	@Override
	protected Material getRelevantInfo(DamageEventInfo eventInfo){ return getRelevantEntity(eventInfo).getLocation().add(0, -1, 0).getBlock().getType();}
	@Override
	protected Material getRelevantInfo(SpawnEventInfo eventInfo){ return getRelevantEntity(eventInfo).getLocation().add(0, -1, 0).getBlock().getType();}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityOnBlock.class, Pattern.compile(ModDamage.entityPart + "onblock\\." + ModDamage.materialRegex, Pattern.CASE_INSENSITIVE));
	}
}
