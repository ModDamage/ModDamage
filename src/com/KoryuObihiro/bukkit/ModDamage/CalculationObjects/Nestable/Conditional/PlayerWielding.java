package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class PlayerWielding extends EntityConditionalCalculation<Material> 
{
	public PlayerWielding(boolean inverted, boolean forAttacker, Material material, List<ModDamageCalculation> calculations)
	{  
		super(inverted, forAttacker, material, calculations);
	}
	@Override
	public Material getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.materialInHand_attacker:eventInfo.materialInHand_target);}
	@Override
	public Material getRelevantInfo(SpawnEventInfo eventInfo){ return null;}
	
	public static void register()
	{
		CalculationUtility.register(PlayerWielding.class, Pattern.compile(CalculationUtility.ifPart + CalculationUtility.entityPart + "wielding\\." + CalculationUtility.materialRegex, Pattern.CASE_INSENSITIVE));
	}	
}
