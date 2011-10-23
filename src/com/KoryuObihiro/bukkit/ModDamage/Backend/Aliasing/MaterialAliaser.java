package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Material;

public class MaterialAliaser extends Aliaser<HashSet<Material>, Material> 
{
	private static final long serialVersionUID = -557230493957602224L;

	public MaterialAliaser() {super("Material");}

	@Override
	protected Material matchNonAlias(String key){ return Material.matchMaterial(key);}

	@Override
	protected String getObjectName(Material object){ return object.name();}

	@Override
	protected HashSet<Material> getNewStorageClass(Material value){ return new HashSet<Material>(Arrays.asList(value));}

	@Override
	protected HashSet<Material> getNewStorageClass(){ return new HashSet<Material>();}
}
