package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

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
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, PlayerWielding.class, Pattern.compile(ModDamage.entityPart + "wielding\\." + ModDamage.materialRegex, Pattern.CASE_INSENSITIVE));
	}	
}
