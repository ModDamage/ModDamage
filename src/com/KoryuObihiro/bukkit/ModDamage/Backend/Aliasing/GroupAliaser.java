package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.Arrays;
import java.util.HashSet;

public class GroupAliaser extends Aliaser<HashSet<String>, String> 
{
	private static final long serialVersionUID = 7539931612104625797L;

	public GroupAliaser() {super("Group");}

	@Override
	protected String matchNonAlias(String key){ return key;}

	@Override
	protected String getObjectName(String groupName){ return "\"" + (groupName.length() > 8?groupName.substring(0, 8):groupName) + "\"";}

	@Override
	protected HashSet<String> getNewStorageClass(String value){ return new HashSet<String>(Arrays.asList(value));}

	@Override
	protected HashSet<String> getNewStorageClass(){ return new HashSet<String>();}
}
