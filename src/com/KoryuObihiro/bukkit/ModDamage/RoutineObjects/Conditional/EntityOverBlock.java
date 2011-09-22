package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalRoutine;

public class EntityOverBlock extends EntityConditionalStatement
{
	final List<Material> materials;
	public EntityOverBlock(boolean inverted, EntityReference entityReference, List<Material> materials)
	{ 
		super(inverted, entityReference);
		this.materials = materials;
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{
		Location entityLocation = entityReference.getEntity(eventInfo).getLocation();
		int x = entityLocation.getBlockX(), y = entityLocation.getBlockY(), z = entityLocation.getBlockZ();
		while(y > 0)
		{
			Material material = eventInfo.world.getBlockAt(x, y, z).getType();
			if(materials.contains(material))
				return true;
			else if(!material.equals(Material.AIR))
				return false;//FIXME Might need tweaking for nonsolid blocks.
			y--;
		}
		return false;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityOverBlock.class, Pattern.compile("(!?)(\\w+)\\.overblock\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityOverBlock getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<Material> matchedItems = ModDamage.matchItemAlias(matcher.group(3));
			if(!matchedItems.isEmpty() && EntityReference.isValid(matcher.group(2)))		
				return new EntityOverBlock(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), matchedItems);
		}
		return null;
	}
}
