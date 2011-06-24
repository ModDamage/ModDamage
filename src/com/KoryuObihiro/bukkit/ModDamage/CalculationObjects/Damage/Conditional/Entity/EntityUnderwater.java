package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional.Entity;

import java.util.List;
import net.minecraft.server.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityUnderwater extends EntityDamageConditionalCalculation 
{
	public EntityUnderwater(boolean forAttacker, List<DamageCalculation> calculations)
	{ 
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo) 
	{
		return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().add(0, 1, 0).getBlock().getType().equals(Material.WATER)
				&& (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlock().getType().equals(Material.WATER);}
	}
