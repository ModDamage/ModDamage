package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;
import net.minecraft.server.Material;
import org.bukkit.entity.LivingEntity;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityUnderwater extends EntityConditionalCalculation 
{
	public EntityUnderwater(boolean forAttacker, List<DamageCalculation> calculations)
	{ 
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public int calculate(LivingEntity target, LivingEntity attacker, int eventDamage) 
	{
		if((forAttacker?attacker:target).getLocation().add(0, 1, 0).getBlock().getType().equals(Material.WATER)
				&& (forAttacker?attacker:target).getLocation().getBlock().getType().equals(Material.WATER))
			return makeCalculations(target, attacker, eventDamage);
		return 0;
	}
}
