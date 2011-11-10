package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class MaterialAliaser extends CollectionAliaser<Material> 
{
	private static final long serialVersionUID = -557230493957602224L;

	public MaterialAliaser() {super("Material");}

	@Override
	protected Material matchNonAlias(String key){ return Material.matchMaterial(key);}

	@Override
	protected String getObjectName(Material object){ return object.name();}
}
