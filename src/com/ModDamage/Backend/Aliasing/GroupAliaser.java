package com.ModDamage.Backend.Aliasing;

import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class GroupAliaser extends CollectionAliaser<String> 
{
	public GroupAliaser() {super(AliasManager.Group.name());}

	@Override
	protected String matchNonAlias(String key){ return key;}
	
	@Override
	protected String getObjectName(String groupName){ return "\"" + (groupName.length() > 8?groupName.substring(0, 8):groupName) + "\"";}
}
