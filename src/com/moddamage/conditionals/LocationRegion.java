package com.moddamage.conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;

import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.alias.RegionAliaser;
import com.moddamage.backend.ExternalPluginManager;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

public class LocationRegion extends Conditional<Location>
{
	public static final Pattern pattern = Pattern.compile("\\.(?:in)?regions?(exact)?\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	private final boolean exact;
	private final Collection<String> regions;
	
	public LocationRegion(IDataProvider<Location> locDP, boolean exact, Collection<String> regions)
	{
		super(Location.class, locDP);
		this.exact = exact;
		this.regions = regions;		
	}

	@Override
	public Boolean get(Location loc, EventData data)
	{
		Collection<String> entityRegions = getRegions(loc);
		if(exact) {
            if (entityRegions.size() == regions.size() && regions.containsAll(entityRegions))
                return true;
		}
		else
		{
			for(String region : entityRegions)
	            if (regions.contains(region))
	                return true;
		}
		return false;
	}
	
	protected Collection<String> getRegions(Location loc) 
	{
		return ExternalPluginManager.getRegions(loc);
	}
	
	@Override
	public String toString()
	{
		return startDP + ".inregion" + (exact ? "exact":"") + "." + Utils.joinBy(",", regions);
	}
	
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Location.class, pattern, new IDataParser<Boolean, Location>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Location> locDP, Matcher m, StringMatcher sm)
				{
					Collection<String> regions = RegionAliaser.match(m.group(2));
					if(regions.isEmpty()) return null;
					
					return new LocationRegion(locDP, m.group(1) != null, regions);
				}
			});
	}
}
