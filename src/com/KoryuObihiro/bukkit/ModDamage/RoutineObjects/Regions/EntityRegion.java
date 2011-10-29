package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Regions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityConditionalStatement;

public class EntityRegion extends EntityConditionalStatement
{
	final boolean inclusiveComparison;
	final HashSet<String> regions;
	public EntityRegion(boolean inverted, boolean inclusiveComparison, EntityReference entityReference, HashSet<String> regions)
	{
		super(inverted, entityReference);
		this.inclusiveComparison = inclusiveComparison;
		this.regions = regions;		
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{
		List<String> entityRegions = getRegions(eventInfo);
		for(String region : entityRegions)
			if(inclusiveComparison?regions.contains(region):(entityRegions.size() == 1 && regions.contains(entityRegions.get(1))))
				return true;
		return false;
	}
	
	protected List<String> getRegions(TargetEventInfo eventInfo) 
	{
		if(entityReference.getEntity(eventInfo) != null)
			return ExternalPluginManager.getRegionsManager().getRegions(entityReference.getEntity(eventInfo).getLocation());//XXX Use .addAll(getEyeLocation())?
		return Arrays.asList();
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityRegion.class, Pattern.compile("(!?)(\\w+)\\.(region|regiononly).(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityRegion getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			HashSet<String> regions = ModDamage.matchRegionAlias(matcher.group(3));
			if(!regions.isEmpty() && EntityReference.isValid(matcher.group(2)))
				return new EntityRegion(matcher.group(1).equalsIgnoreCase("!"), matcher.group(3).endsWith("only"), EntityReference.match(matcher.group(2)), regions);
		}
		return null;
	}
}
