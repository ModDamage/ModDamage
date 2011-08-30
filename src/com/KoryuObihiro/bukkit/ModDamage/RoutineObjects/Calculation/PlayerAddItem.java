package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class PlayerAddItem extends PlayerCalculatedEffectRoutine
{
	protected final List<Material> materials;
	public PlayerAddItem(String configString, boolean forAttacker, List<Material> materials, List<Routine> routines)
	{
		super(configString, forAttacker, routines);
		this.materials = materials;
	}
	
	@Override
	protected void applyEffect(Player affectedObject, int input) 
	{
		for(Material material : materials)
			affectedObject.getInventory().addItem(new ItemStack(material, input));
	}

	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(PlayerAddItem.class, Pattern.compile("(\\w+)effect\\.addItem\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerAddItem getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
		{
			List<Material> materials = ModDamage.matchItemAlias(matcher.group(2));
			if(!materials.isEmpty())
				return new PlayerAddItem(matcher.group(), (ModDamage.matchesValidEntity(matcher.group(1)))?ModDamage.matchEntity(matcher.group(1)):false, materials, routines);
		}
		return null;
	}
}
