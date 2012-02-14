package com.ModDamage.Backend.Aliasing;

import java.util.Collection;

import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class GroupAliaser extends CollectionAliaser<String> 
{
	public static GroupAliaser aliaser = new GroupAliaser();
	public static Collection<String> match(String string) { return aliaser.matchAlias(string); }
	
	public GroupAliaser() { super(AliasManager.Group.name()); }

	@Override
	protected String matchNonAlias(String key){ return key; }
	
	//@Override
	//protected String getObjectName(String groupName){ return "\"" + (groupName.length() > 8?groupName.substring(0, 8):groupName) + "\""; }
}
