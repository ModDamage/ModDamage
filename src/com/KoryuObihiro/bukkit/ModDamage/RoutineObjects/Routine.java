package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

abstract public class Routine
{		
	abstract public void run(DamageEventInfo eventInfo);
	abstract public void run(SpawnEventInfo eventInfo);
}