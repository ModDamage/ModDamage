package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;

public class PlayerWielding extends EntityConditionalStatement<Material> 
{
	public PlayerWielding(boolean inverted, boolean forAttacker, Material material)
	{  
		super(inverted, forAttacker, material);
	}
	@Override
	public Material getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.materialInHand_attacker:eventInfo.materialInHand_target);}
	@Override
	public Material getRelevantInfo(SpawnEventInfo eventInfo){ return null;}
	
	public static void register()
	{
		ConditionalCalculation.registerStatement(PlayerWielding.class, Pattern.compile(CalculationUtility.entityPart + "wielding\\." + CalculationUtility.materialRegex, Pattern.CASE_INSENSITIVE));
	}	
}
