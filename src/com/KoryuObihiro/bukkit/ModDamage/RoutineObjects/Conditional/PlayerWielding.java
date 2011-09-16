package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class PlayerWielding extends PlayerConditionalStatement 
{
	final List<Material> materials;
	public PlayerWielding(boolean inverted, EntityReference entityReference, List<Material> materials)
	{  
		super(inverted, entityReference);
		this.materials = materials;
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo){ return materials.contains(entityReference.getMaterial(eventInfo));}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, PlayerWielding.class, Pattern.compile("(!?)(\\w+)\\.wielding\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}	
	
	public static PlayerWielding getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<Material> matchedItems = ModDamage.matchItemAlias(matcher.group(3));
			if(!matchedItems.isEmpty())
				return new PlayerWielding(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), matchedItems);
		}
		return null;
	}
}
