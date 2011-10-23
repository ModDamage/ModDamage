package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;

public class ArmorAliaser extends Aliaser<List<ArmorSet>, ArmorSet> 
{
	private static final long serialVersionUID = 1304321966061887438L;

	public ArmorAliaser(){ super("Armor");}

	@Override
	protected ArmorSet matchNonAlias(String key)
	{ 
		ArmorSet armorSet = new ArmorSet(key);
		return (armorSet.isValid()?armorSet:null);
	}

	@Override
	protected String getObjectName(ArmorSet object){ return object.toString();}

	@Override
	protected List<ArmorSet> getNewStorageClass(ArmorSet value){ return Arrays.asList(value);}

	@Override
	protected List<ArmorSet> getNewStorageClass(){ return new ArrayList<ArmorSet>();}
}
