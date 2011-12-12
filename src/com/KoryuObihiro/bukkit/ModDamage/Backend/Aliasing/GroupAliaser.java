package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class GroupAliaser extends CollectionAliaser<String> 
{
	private static final long serialVersionUID = 7539931612104625797L;

	public GroupAliaser() {super("Group");}

	@Override
	protected String matchNonAlias(String key){ return key;}
	
	@Override
	protected String getObjectName(String groupName){ return "\"" + (groupName.length() > 8?groupName.substring(0, 8):groupName) + "\"";}
}
