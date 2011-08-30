package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

public class MessageAliaser extends Aliaser<String> 
{
	private static final long serialVersionUID = 7539931612104625797L;

	public MessageAliaser() {super("Message");}

	@Override
	protected String matchNonAlias(String key){ return key;} //TODO 0.9.5 Add color.

	@Override
	protected String getObjectName(String object){ return object;}
}
