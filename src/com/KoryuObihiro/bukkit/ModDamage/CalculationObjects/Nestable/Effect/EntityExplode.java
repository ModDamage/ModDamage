package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityExplode extends EntityEffectCalculation
{
	public EntityExplode(boolean forAttacker, List<ModDamageCalculation> calculations){ super(forAttacker, calculations);}
	
	@Override
	void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.getWorld().createExplosion(affectedObject.getLocation(), input);
	}
	
	public static void register()
	{
		CalculationUtility.register(EntityExplode.class, Pattern.compile(CalculationUtility.entityPart + "effect\\.explode", Pattern.CASE_INSENSITIVE));
	}
}
