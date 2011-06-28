package com.KoryuObihiro.bukkit.ModDamage;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class ModDamageEntityListener extends EntityListener
{
	ModDamage plugin;
	public ModDamageEntityListener(ModDamage plugin){ this.plugin = plugin;}
	
	@Override
	public void onEntityDamage(EntityDamageEvent event){ if (ModDamage.isEnabled && !event.isCancelled() && (event.getEntity() instanceof LivingEntity)) plugin.handleDamageEvent(event);}
	
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event){ if (ModDamage.isEnabled && !event.isCancelled()) plugin.handleSpawnEvent(event);}
}
