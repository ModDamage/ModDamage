package com.ModDamage.Conditionals;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.Alias.RegionAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class EntityRegion extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.inregion(only)?.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	private final DataRef<Entity> entityRef;
	private final boolean inclusiveComparison;
	private final Collection<String> regions;
	
	public EntityRegion(String configString, boolean inclusiveComparison, DataRef<Entity> entityRef, Collection<String> regions)
	{
		super(configString);
		this.entityRef = entityRef;
		this.inclusiveComparison = inclusiveComparison;
		this.regions = regions;		
	}

	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		Collection<String> entityRegions = getRegions(data);
		for(String region : entityRegions)
			if(inclusiveComparison?regions.contains(region):(entityRegions.size() == regions.size() && regions.containsAll(entityRegions)))
				return true;
		return false;
	}
	
	protected Collection<String> getRegions(EventData data) 
	{
		if(entityRef.get(data) != null)
			return ExternalPluginManager.getRegionsManager().getRegions(entityRef.get(data).getLocation());//XXX Use .addAll(getEyeLocation())?
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
		public EntityRegion getNew(Matcher matcher, EventInfo info)
		{
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());
			Collection<String> regions = RegionAliaser.match(matcher.group(3));
			if(!regions.isEmpty() && entityRef != null)
				return new EntityRegion(matcher.group(), matcher.group(2) != null, entityRef, regions);
			return null;
		}
	}
}
