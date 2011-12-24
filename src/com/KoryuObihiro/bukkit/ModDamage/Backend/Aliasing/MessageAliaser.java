package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Parameterized.Message.DynamicMessage;

public class MessageAliaser extends CollectionAliaser<DynamicMessage> 
{
	public MessageAliaser() {super(AliasManager.Message.name());}
	@Override
	protected DynamicMessage matchNonAlias(String key){ return new DynamicMessage(key);}
	@Override
	protected String getObjectName(DynamicMessage object){ return object.toString();}
}
