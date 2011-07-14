package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;

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
	
	public static void register()
	{
		ConditionalCalculation.registerStatement(EntityOnBlock.class, Pattern.compile(CalculationUtility.entityPart + "onblock\\." + CalculationUtility.materialRegex, Pattern.CASE_INSENSITIVE));
	}
}
