package com.moddamage.events;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.moddamage.MDEvent;
import com.moddamage.ModDamage;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;

public class Init extends MDEvent
{
	private Init() { super(myInfo); }
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class,	"entity",
			World.class,	"world");
	
	public static Init instance = new Init();
	
	public static void onInit(Entity entity)
	{
		if(!ModDamage.isEnabled) return;
		
		EventData data = myInfo.makeData(
				entity,
				entity.getWorld());
		
		instance.runRoutines(data);
	}
	
	public static void initAll()
	{
		if(!ModDamage.isEnabled) return;
		
		for (World world : Bukkit.getWorlds())
			for (Entity entity : world.getEntities())
				onInit(entity);
	}
}
