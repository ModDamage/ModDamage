package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class PlayerAddItem extends PlayerEffectCalculation
{
	protected Material material;
	public PlayerAddItem(boolean forAttacker, List<ModDamageCalculation> calculations){ super(forAttacker, calculations);}
	
	@Override
	void applyEffect(Player affectedObject, int input) 
	{
		affectedObject.getInventory().addItem(new ItemStack(affectedObject.getItemInHand().getType(), affectedObject.getItemInHand().getAmount() + input));
	}

	public static void register()
	{
		CalculationUtility.register(PlayerAddItem.class, Pattern.compile(CalculationUtility.entityPart + "effect\\.addItem\\." + CalculationUtility.materialRegex + "\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
