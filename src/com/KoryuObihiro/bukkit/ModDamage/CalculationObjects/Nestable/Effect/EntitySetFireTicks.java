package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntitySetFireTicks extends EntityEffectCalculation
{
	public EntitySetFireTicks(boolean forAttacker, List<ModDamageCalculation> calculations){ super(forAttacker, calculations);}

	@Override
	void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setFireTicks(input);
	}

	public static void register()
	{
		CalculationUtility.register(EntitySetFireTicks.class, Pattern.compile(CalculationUtility.entityPart + "effect\\.setfireticks", Pattern.CASE_INSENSITIVE));
	}
}
