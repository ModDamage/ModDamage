package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class PlayerAddItem extends PlayerCalculatedEffectRoutine
{
	protected Material material;
	public PlayerAddItem(boolean forAttacker, List<Routine> calculations){ super(forAttacker, calculations);}
	
	@Override
	protected void applyEffect(Player affectedObject, int input) 
	{
		affectedObject.getInventory().addItem(new ItemStack(affectedObject.getItemInHand().getType(), affectedObject.getItemInHand().getAmount() + input));
	}

	public static void register(RoutineUtility routineUtility)
	{
		routineUtility.registerBase(PlayerAddItem.class, Pattern.compile(RoutineUtility.entityPart + "effect\\.addItem\\." + RoutineUtility.materialRegex + "\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
