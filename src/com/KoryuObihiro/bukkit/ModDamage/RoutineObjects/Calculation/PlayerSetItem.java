package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class PlayerSetItem extends PlayerCalculationRoutine
{
	protected final Material material;
	public PlayerSetItem(String configString, EntityReference entityReference, Material material, IntegerMatch match)
	{
		super(configString, entityReference, match);
		this.material = material;
	}
	
	@Override
	protected void applyEffect(Player affectedObject, int input) 
	{
		affectedObject.setItemInHand(new ItemStack(material, input));
	}
	
	public static void register()
	{
		CalculationRoutine.registerCalculation(PlayerSetItem.class, Pattern.compile("(\\w+)effect\\.setItem\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerSetItem getNew(Matcher matcher, IntegerMatch match)
	{
		if(matcher != null && match != null)
		{
			Material material = Material.matchMaterial(matcher.group(2));
			if(material != null && EntityReference.isValid(matcher.group(1)))
				return new PlayerSetItem(matcher.group(), EntityReference.match(matcher.group(1)), material, match);//FIXME 0.9.7 - ItemStack routine
		}
		return null;
	}
}