package com.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.AliasManager;
import com.ModDamage.RoutineObjects.Nested.Conditional;

public class EntityRegion extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.inregion(only)?.(\\w+)", Pattern.CASE_INSENSITIVE);
	final EntityReference entityReference;
	final boolean inclusiveComparison;
	final Collection<String> regions;
	public EntityRegion(boolean inclusiveComparison, EntityReference entityReference, Collection<String> regions)
	{
		this.entityReference = entityReference;
		this.inclusiveComparison = inclusiveComparison;
		this.regions = regions;		
	}

	@Override
	public boolean evaluate(TargetEventInfo eventInfo)
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
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EntityRegion getNew(Matcher matcher)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			Collection<String> regions = AliasManager.matchRegionAlias(matcher.group(3));
			if(!regions.isEmpty() && reference != null)
				return new EntityRegion(matcher.group(2) != null, reference, regions);
			return null;
		}
	}
}
