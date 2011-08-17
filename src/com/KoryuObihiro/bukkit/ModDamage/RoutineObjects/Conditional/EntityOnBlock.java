package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityOnBlock extends EntityConditionalStatement<List<Material>>
{
	public EntityOnBlock(boolean inverted, boolean forAttacker, List<Material> materials)
	{ 
		super(inverted, forAttacker, materials);
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{
		return (eventInfo.getRelevantEntity(forAttacker)!= null)?value.contains(eventInfo.getRelevantEntity(forAttacker).getLocation().add(0, -1, 0).getBlock().getType()):false;
	}
	@Override
	protected List<Material> getRelevantInfo(TargetEventInfo eventInfo){ return null;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityOnBlock.class, Pattern.compile("(!?)(\\w+)\\.onblock\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityOnBlock getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<Material> matchedItems = ModDamage.matchItemAlias(matcher.group(3));
			if(!matchedItems.isEmpty())
				return new EntityOnBlock(matcher.group(1).equalsIgnoreCase("!"), (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false, matchedItems);
		}
		return null;
	}
}
