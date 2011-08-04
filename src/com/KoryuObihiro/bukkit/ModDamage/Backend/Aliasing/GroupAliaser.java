package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

public class GroupAliaser extends Aliaser<String> 
{
	private static final long serialVersionUID = 7539931612104625797L;

	public GroupAliaser() {super("Group");}

	@Override
	protected String matchNonAlias(String key){ return key;}

	@Override
	protected String getName(String object){ return "\"" + object.substring(0, 8) + "\"";}
}
