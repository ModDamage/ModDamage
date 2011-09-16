package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityRegionOnly extends EntityConditionalStatement<List<String>>
{
	public EntityRegionOnly(boolean inverted, boolean forAttacker, List<String> regions)
	{
		super(inverted, forAttacker, regions);
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{
		List<String> regions = getRelevantInfo(eventInfo);
		return regions.size() == 1 && value.contains(regions.get(0));
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
		ConditionalRoutine.registerStatement(routineUtility, EntityRegionOnly.class, Pattern.compile("(!?)(\\w+)\\.regiononly.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityRegionOnly getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<String> regions = ModDamage.matchRegionAlias(matcher.group(3));
			if(!regions.isEmpty())
				return new EntityRegionOnly(matcher.group(1).equalsIgnoreCase("!"), (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false, regions);
		}
		return null;
	}
}
