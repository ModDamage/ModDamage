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
	public int calculate(LivingEntity entity, int eventDamage) 
	{
		if(entity.getLocation().add(0, 1, 0).getBlock().getType().equals(Material.WATER)
				&& entity.getLocation().getBlock().getType().equals(Material.WATER))
			return makeCalculations(calculations, eventDamage);
		return 0;
	}
	@Override
	public int calculate(int eventDamage){ return 0;}
}
