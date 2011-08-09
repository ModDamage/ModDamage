package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityDropItem extends EntityCalculatedEffectRoutine
{
	protected final List<Material> materials;
	public EntityDropItem(boolean forAttacker, List<Material> materials, List<Routine> routines)
	{ 
		super(forAttacker, routines);
		this.materials = materials;
	}
	
	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		for(Material material : materials)
			affectedObject.getWorld().dropItem(affectedObject.getLocation(), new ItemStack(material, input));//TODO Just override run(), ter summat.
	}

	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(EntityDropItem.class, Pattern.compile(ModDamage.entityRegex + "effect\\.dropItem\\." + ModDamage.materialRegex, Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityDropItem getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
		{
			List<Material> materials =  ModDamage.matchItemAlias(matcher.group(2));
			if(!materials.isEmpty())
				return new EntityDropItem(matcher.group(1).equalsIgnoreCase("attacker"), materials, routines);
		}
		return null;
	}
}
