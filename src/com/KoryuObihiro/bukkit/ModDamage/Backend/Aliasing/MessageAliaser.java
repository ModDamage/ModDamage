package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Message.DynamicMessage;

public class MessageAliaser extends Aliaser<List<DynamicMessage>, DynamicMessage> 
{
	private static final long serialVersionUID = 7539931612104625797L;

	public MessageAliaser() {super("Message");}

	@Override
	protected DynamicMessage matchNonAlias(String key){ return new DynamicMessage(key);}

	@Override
	protected String getObjectName(DynamicMessage object){ return object.toString();}

	@Override
	protected List<DynamicMessage> getNewStorageClass(DynamicMessage value){ return Arrays.asList(value);}

	@Override
	protected List<DynamicMessage> getNewStorageClass(){ return new ArrayList<DynamicMessage>();}
}
