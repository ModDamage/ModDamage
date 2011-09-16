package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;

public class RegionAliaser extends Aliaser<String> 
{
	private static final long serialVersionUID = 6446417315016119118L;

	public RegionAliaser() {super("Region");}

	@Override
	protected String matchNonAlias(String key)
	{
		if(ExternalPluginManager.regionsManager.getAllRegions().contains(key))
			return key;
		return null;
	}

	@Override
	protected String getObjectName(String object){ return object;}
}
