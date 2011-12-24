package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;

public class RegionAliaser extends CollectionAliaser<String> 
{
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
