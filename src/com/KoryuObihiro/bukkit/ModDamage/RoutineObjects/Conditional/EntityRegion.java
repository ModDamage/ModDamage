package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityRegion extends EntityConditionalStatement<List<String>>
{
	public EntityRegion(boolean inverted, boolean forAttacker, List<String> regions)
	{
		super(inverted, forAttacker, regions);
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{
		for(String region : getRelevantInfo(eventInfo))
			if(value.contains(region))
				return true;
		return false;
	}
	
	@Override
	protected List<String> getRelevantInfo(TargetEventInfo eventInfo) 
	{
		if(eventInfo.getRelevantEntity(forAttacker) != null)
			return ModDamage.regionsManager.getRegions(eventInfo.getRelevantEntity(forAttacker).getLocation());//XXX Use .addAll(getEyeLocation())?
		return ModDamage.emptyList;
	}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityRegion.class, Pattern.compile("(!?)(\\w+)\\.region.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityRegion getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<String> regions = ModDamage.matchRegionAlias(matcher.group(3));
			if(!regions.isEmpty())
				return new EntityRegion(matcher.group(1).equalsIgnoreCase("!"), (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false, regions);
		}
		return null;
	}
}
