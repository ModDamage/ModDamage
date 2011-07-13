package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntitySetAirTicks extends EntityEffectCalculation
{
	public EntitySetAirTicks(boolean forAttacker, List<ModDamageCalculation> calculations){ super(forAttacker, calculations);}

	@Override
	void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setRemainingAir(input);
	}
	
	public static void register()
	{
		CalculationUtility.register(EntitySetAirTicks.class, Pattern.compile(CalculationUtility.entityPart + "effect\\.setairticks", Pattern.CASE_INSENSITIVE));
	}
}
