package com.ModDamage.Backend.Aliasing;

import java.util.Collection;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class RegionAliaser extends CollectionAliaser<String> 
{
	static RegionAliaser aliaser = new RegionAliaser();
	public static Collection<String> match(String string) { return aliaser.matchAlias(string); }
	
	public RegionAliaser() {super(AliasManager.Region.name());}

	@Override
	protected String matchNonAlias(String key)
	{
		if(!ExternalPluginManager.getRegionsManager().getAllRegions().contains(key))
			ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, "Warning: region \"" + key + "\" does not currently exist.");
		return key;
	}

	@Override
	protected String getObjectName(String object){ return object;}
}
