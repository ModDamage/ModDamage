package com.moddamage.alias;

import java.util.Collection;

import com.moddamage.LogUtil;
import com.moddamage.alias.Aliaser.CollectionAliaser;
import com.moddamage.backend.ExternalPluginManager;

public class RegionAliaser extends CollectionAliaser<String> 
{
	public static RegionAliaser aliaser = new RegionAliaser();
	public static Collection<String> match(String string) { return aliaser.matchAlias(string); }
	
	public RegionAliaser() { super(AliasManager.Region.name()); }

	@Override
	protected String matchNonAlias(String key)
	{
		if(!ExternalPluginManager.getAllRegions().contains(key))
			LogUtil.warning_strong("Warning: region \"" + key + "\" does not currently exist.");
		return key;
	}

	//@Override
	//protected String getObjectName(String object){ return object; }
}
