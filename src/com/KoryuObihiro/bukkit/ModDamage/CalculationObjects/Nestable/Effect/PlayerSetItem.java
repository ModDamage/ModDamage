package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class PlayerSetItem extends PlayerEffectCalculation
{
	protected final Material material;
	public PlayerSetItem(boolean forAttacker, Material material, List<ModDamageCalculation> calculations)
	{
		super(forAttacker, calculations);
		this.material = material;
	}
	
	@Override
	void applyEffect(Player affectedObject, int input) 
	{
		affectedObject.setItemInHand(new ItemStack(material, input));
	}
	
	public static void register()
	{
		CalculationUtility.register(PlayerSetItem.class, Pattern.compile(CalculationUtility.entityPart + "effect\\.addItem" + CalculationUtility.materialRegex + "\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
