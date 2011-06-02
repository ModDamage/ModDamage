package com.KoryuObihiro.bukkit.ModDamage;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;


public class ModDamageEntityListener extends EntityListener
{
//Members
	private ModDamage plugin;
	//Constructors	
	public ModDamageEntityListener(ModDamage plugin) 
	{
		this.plugin = plugin;
	}
	
//Functions
	@Override
	public void onEntityDamage(EntityDamageEvent event){ plugin.passDamageEvent(event);}
	
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event){ plugin.passSpawnEvent(event);}
}
