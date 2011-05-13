package com.KoryuObihiro.bukkit.ModDamage;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ModDamagePlayerListener extends PlayerListener
{
	//Members
	private ModDamage plugin;
	//Constructors	
	public ModDamagePlayerListener(ModDamage plugin) {this.plugin = plugin;}
	
//Functions
	
	//TODO Make sure this actually works.
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		
	}
	
	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		//TODO Add health changing for players?
	}
	//WHEN PLAYER CHANGES WORLD DO THIS
}
