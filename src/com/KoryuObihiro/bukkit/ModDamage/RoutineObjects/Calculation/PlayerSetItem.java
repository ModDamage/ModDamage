package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class PlayerSetItem extends PlayerCalculationRoutine
{
	protected final Material material;
	public PlayerSetItem(String configString, EntityReference entityReference, Material material, List<Routine> routines)
	{
		super(configString, entityReference, routines);
		this.material = material;
	}
	
	@Override
	protected void applyEffect(Player affectedObject, int input) 
	{
		affectedObject.setItemInHand(new ItemStack(material, input));
	}
	
	public static void register()
	{
		CalculationRoutine.registerStatement(PlayerSetItem.class, Pattern.compile("(\\w+)effect\\.setItem\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerSetItem getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
		{
			Material material = Material.matchMaterial(matcher.group(2));
			if(material != null && EntityReference.isValid(matcher.group(1)))
				return new PlayerSetItem(matcher.group(), EntityReference.match(matcher.group(1)), material, routines);
		}
		return null;
	}
}
