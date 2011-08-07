package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class DropItem extends PlayerCalculatedEffectRoutine
{
	protected final Material material;
	public DropItem(boolean forAttacker, Material material, List<Routine> routines)
	{ 
		super(forAttacker, routines);
		this.material = material;
	}
	
	@Override
	protected void applyEffect(Player affectedObject, int input) 
	{
		affectedObject.getWorld().dropItem(affectedObject.getLocation(), new ItemStack(material, input));//TODO Just override run(), ter summat.
	}

	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(DropItem.class, Pattern.compile(ModDamage.entityRegex + "effect\\.dropItem\\." + ModDamage.materialRegex, Pattern.CASE_INSENSITIVE));
	}
	
	public static DropItem getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
			return new DropItem(matcher.group(1).equalsIgnoreCase("attacker"), Material.matchMaterial(matcher.group(2)), routines);
		return null;
	}
}
