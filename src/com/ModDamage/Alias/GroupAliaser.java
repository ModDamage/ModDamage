package com.ModDamage.Alias;

import java.util.Collection;

import com.ModDamage.Alias.Aliaser.CollectionAliaser;
import com.ModDamage.Backend.ScriptLine;

public class GroupAliaser extends CollectionAliaser<String> 
{
	public static GroupAliaser aliaser = new GroupAliaser();
	public static Collection<String> match(ScriptLine scriptLine) { return aliaser.matchAlias(scriptLine); }
	public static Collection<String> match(ScriptLine scriptLine, String string) { return aliaser.matchAlias(scriptLine, string); }
	
	public GroupAliaser() { super(AliasManager.Group.name()); }

	@Override
	protected String matchNonAlias(String key){ return key; }
	
	//@Override
	//protected String getObjectName(String groupName){ return "\"" + (groupName.length() > 8?groupName.substring(0, 8):groupName) + "\""; }
}
