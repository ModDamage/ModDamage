package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class PlayerSetItem extends PlayerCalculatedEffectRoutine
{
	protected final Material material;
	public PlayerSetItem(String configString, boolean forAttacker, Material material, List<Routine> routines)
	{
		super(configString, forAttacker, routines);
		this.material = material;
	}
	
	@Override
	protected void applyEffect(Player affectedObject, int input) 
	{
		affectedObject.setItemInHand(new ItemStack(material, input));
	}
	
	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(PlayerSetItem.class, Pattern.compile("(\\w+)effect\\.setItem\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerSetItem getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
		{
			Material material = Material.matchMaterial(matcher.group(2));
			if(material != null)
				return new PlayerSetItem(matcher.group(), (ModDamage.matchesValidEntity(matcher.group(1)))?ModDamage.matchEntity(matcher.group(1)):false, material, routines);
		}
		return null;
	}
}
