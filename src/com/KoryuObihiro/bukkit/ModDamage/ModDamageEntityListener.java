package com.KoryuObihiro.bukkit.ModDamage;

import org.bukkit.entity.LivingEntity;
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
	public void onEntityDamage(EntityDamageEvent event){ if (!event.isCancelled() && (event.getEntity() instanceof LivingEntity)) plugin.passDamageEvent(event);}
	
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event){ if (!event.isCancelled()) plugin.passSpawnEvent(event);}
}
