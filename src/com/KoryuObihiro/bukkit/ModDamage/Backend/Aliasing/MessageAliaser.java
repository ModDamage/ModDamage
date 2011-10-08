package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Message.DynamicMessage;

public class MessageAliaser extends Aliaser<DynamicMessage> 
{
	private static final long serialVersionUID = 7539931612104625797L;

	public MessageAliaser() {super("Message");}

	@Override
	protected DynamicMessage matchNonAlias(String key){ return new DynamicMessage(key);}

	@Override
	protected String getObjectName(DynamicMessage object){ return object.toString();}
}
