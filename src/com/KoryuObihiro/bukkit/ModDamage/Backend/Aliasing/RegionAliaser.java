package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class RegionAliaser extends CollectionAliaser<String> 
{
	private static final long serialVersionUID = 6446417315016119118L;

	public RegionAliaser() {super("Region");}

	@Override
	protected String matchNonAlias(String key)
	{
		if(ExternalPluginManager.getRegionsManager().getAllRegions().contains(key))
			return key;
		return null;
	}

	@Override
	protected String getObjectName(String object){ return object;}
}
