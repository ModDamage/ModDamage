package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.Arrays;
import java.util.HashSet;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;

public class RegionAliaser extends Aliaser<HashSet<String>, String> 
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

	@Override
	protected HashSet<String> getNewStorageClass(String value){ return new HashSet<String>(Arrays.asList(value));}

	@Override
	protected HashSet<String> getNewStorageClass(){ return new HashSet<String>();}
}
