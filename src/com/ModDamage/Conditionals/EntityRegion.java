package com.ModDamage.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.StringMatcher;
import com.ModDamage.Alias.RegionAliaser;
import com.ModDamage.Backend.ExternalPluginManager;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class EntityRegion extends Conditional<Entity>
{
	public static final Pattern pattern = Pattern.compile("\\.(?:in)?region(only)?.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	private final boolean inclusiveComparison;
	private final Collection<String> regions;
	
	public EntityRegion(IDataProvider<Entity> entityDP, boolean inclusiveComparison, Collection<String> regions)
	{
		super(Entity.class, entityDP);
		this.inclusiveComparison = inclusiveComparison;
		this.regions = regions;		
	}

	@Override
	public Boolean get(Entity entity, EventData data)
	{
		Collection<String> entityRegions = getRegions(entity);
		for(String region : entityRegions)
			if(inclusiveComparison) {
				if (regions.contains(region))
					return true;
			}
			else
			{
				if (entityRegions.size() == regions.size() && regions.containsAll(entityRegions))
					return true;
			}
		return false;
	}
	
	protected Collection<String> getRegions(Entity entity) 
	{
		return ExternalPluginManager.getRegionsManager().getRegions(entity.getLocation());
	}
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Entity.class, pattern, new IDataParser<Boolean, Entity>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm)
				{
					Collection<String> regions = RegionAliaser.match(m.group(2));
					if(regions.isEmpty()) return null;
					
					return new EntityRegion(entityDP, m.group(1) != null, regions);
				}
			});
	}
}
