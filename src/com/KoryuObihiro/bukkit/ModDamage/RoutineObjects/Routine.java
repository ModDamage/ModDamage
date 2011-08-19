package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

abstract public class Routine
{
	final String configString;
	protected Routine(String configString)
	{
		this.configString = configString;
	}
	public final String getConfigString(){ return configString;}
	abstract public void run(TargetEventInfo eventInfo);
}