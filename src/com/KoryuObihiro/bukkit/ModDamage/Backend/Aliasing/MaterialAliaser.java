package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class MaterialAliaser extends CollectionAliaser<Material> 
{
	public MaterialAliaser() {super(AliasManager.Material.name());}

	@Override
	protected Material matchNonAlias(String key){ return Material.matchMaterial(key);}

	@Override
	protected String getObjectName(Material object){ return object.name();}
}
