package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class EntityDropItem extends EntityCalculationRoutine<Entity>
{
	protected final List<Material> materials;
	public EntityDropItem(String configString, EntityReference entityReference, List<Material> materials, IntegerMatch match)
	{
		super(configString, entityReference, match);
		this.materials = materials;
	}
	
	@Override
	protected void applyEffect(Entity emtity, int input) 
	{
		for(Material material : materials)
			emtity.getWorld().dropItem(emtity.getLocation(), new ItemStack(material, input));
	}

	public static void register()
	{
		CalculationRoutine.registerCalculation(EntityDropItem.class, Pattern.compile("(\\w+)effect\\.dropItem\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityDropItem getNew(Matcher matcher, IntegerMatch match)
	{
		if(matcher != null && match != null)
		{
			List<Material> materials =  ModDamage.matchMaterialAlias(matcher.group(2));
			if(!materials.isEmpty() && EntityReference.isValid(matcher.group(1)))
				return new EntityDropItem(matcher.group(), EntityReference.match(matcher.group(1)), materials, match);
		}
		return null;
	}
}