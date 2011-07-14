package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public class SpawnEventInfo
{
	Logger log = Logger.getLogger("Minecraft");
	public final Server server = ModDamage.server;
	String[] emptyStringArray = {};
	
	public int eventHealth;
	public final World world;
	public final Environment environment;
	public final DamageElement element;
	public final LivingEntity entity;
	public final String name;
	public final String[] groups;
	
//CONSTRUCTORS
	public SpawnEventInfo(Player player) 
	{
		eventHealth = player.getHealth();
		entity = player;
		element = DamageElement.GENERIC_HUMAN;
		name = player.getName();
		groups = (ModDamage.using_Permissions?ModDamage.multigroupPermissions
					?ModDamage.Permissions.getGroups(player.getWorld().getName(), player.getName())
					:ModDamage.Permissions.getGroup(player.getWorld().getName(), player.getName()).split(" "):emptyStringArray);
		
		world = entity.getWorld();
		environment = world.getEnvironment();
	}
	
	public SpawnEventInfo(LivingEntity entity) 
	{
		eventHealth = entity.getHealth();
		this.entity = entity;
		element= DamageElement.matchMobType(entity);
		name = null;
		groups = null;
		
		world = entity.getWorld();	
		environment = world.getEnvironment();
	}
}