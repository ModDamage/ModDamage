package com.ModDamage.Backend.Aliasing;

import java.util.Collection;

import org.bukkit.Material;

import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class MaterialAliaser extends CollectionAliaser<Material> 
{
	public static MaterialAliaser aliaser = new MaterialAliaser();
	public static Collection<Material> match(String string) { return aliaser.matchAlias(string); }
	
	public MaterialAliaser() { super(AliasManager.Material.name()); }

	@Override
	protected Material matchNonAlias(String key){ return Material.matchMaterial(key); }

	//@Override
	//protected String getObjectName(Material object){ return object.name(); }
}
