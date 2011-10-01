package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.mcMMO;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public abstract class McMMOCalculationRoutine extends CalculationRoutine<Player>
{
	final EntityReference entityReference;
	protected McMMOCalculationRoutine(String configString, IntegerMatch match, EntityReference entityReference)
	{
		super(configString, match);
		this.entityReference = entityReference;
	}
}
