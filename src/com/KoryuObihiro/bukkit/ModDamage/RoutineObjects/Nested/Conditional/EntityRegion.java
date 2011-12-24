package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalStatement;

public class EntityRegion extends EntityConditionalStatement
{
	final boolean inclusiveComparison;
	final Collection<String> regions;
	public EntityRegion(boolean inverted, boolean inclusiveComparison, EntityReference entityReference, Collection<String> regions)
	{
		super(inverted, entityReference);
		this.inclusiveComparison = inclusiveComparison;
		this.regions = regions;		
	}

	@Override
	public boolean condition(TargetEventInfo eventInfo)
	{
		Collection<String> entityRegions = getRegions(eventInfo);
		for(String region : entityRegions)
			if(inclusiveComparison?regions.contains(region):(entityRegions.size() == regions.size() && regions.containsAll(entityRegions)))
				return true;
		return false;
	}
	
	protected Collection<String> getRegions(TargetEventInfo eventInfo) 
	{
		if(entityReference.getEntity(eventInfo) != null)
			return ExternalPluginManager.getRegionsManager().getRegions(entityReference.getEntity(eventInfo).getLocation());//XXX Use .addAll(getEyeLocation())?
		return Arrays.asList();
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)(\\w+)\\.in(region|regiononly).(\\w+)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public EntityRegion getNew(Matcher matcher)
		{
			Collection<String> regions = AliasManager.matchRegionAlias(matcher.group(4));
			EntityReference reference = EntityReference.match(matcher.group(2));
			if(!regions.isEmpty() && reference != null)
				return new EntityRegion(matcher.group(1).equalsIgnoreCase("!"), matcher.group(3).endsWith("only"), reference, regions);
			return null;
		}
	}
}
