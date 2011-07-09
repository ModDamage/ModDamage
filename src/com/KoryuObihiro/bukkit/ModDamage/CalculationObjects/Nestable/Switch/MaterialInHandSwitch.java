package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Handlers.ServerHandler;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class MaterialInHandSwitch extends EntitySwitchCalculation<Material>
{
	public MaterialInHandSwitch(boolean forAttacker, LinkedHashMap<Material, List<ModDamageCalculation>> switchStatements) 
	{
		super(forAttacker, switchStatements);
		
		for(Material material : ServerHandler.matchItems(key))
		{
			
		}
	}

	@Override
	protected Material getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.materialInHand_attacker:eventInfo.materialInHand_target);}

	@Override
	protected Material getRelevantInfo(SpawnEventInfo eventInfo){ return null;}

}
