package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityOnBlock extends EntityConditionalStatement
{
	final List<Material> materials;
	public EntityOnBlock(boolean inverted, EntityReference entityReference, List<Material> materials)
	{ 
		super(inverted, entityReference);
		this.materials = materials;
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{
		return materials.contains(entityReference.getEntity(eventInfo).getLocation().add(0, -1, 0).getBlock().getType());
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityOnBlock.class, Pattern.compile("(!?)(\\w+)\\.onblock\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityOnBlock getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<Material> matchedItems = ModDamage.matchItemAlias(matcher.group(3));
			if(!matchedItems.isEmpty() && EntityReference.isValid(matcher.group(2)))		
				return new EntityOnBlock(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), matchedItems);
		}
		return null;
	}
}
