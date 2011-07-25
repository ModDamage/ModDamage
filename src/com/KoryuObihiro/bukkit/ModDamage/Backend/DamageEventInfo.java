package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

@SuppressWarnings("deprecation")
public class DamageEventInfo
{
	Logger log = Logger.getLogger("Minecraft");
	public final Server server = ModDamage.server;
	String[] emptyStringArray = {};
	
	public int eventDamage;
	public final World world;
	public final Environment environment;
	
	//Having everything public may not be a good idea, but I don't intend to change anything later. Do you?
	public final RangedElement rangedElement;
	
	public final ModDamageElement element_target;
	public final LivingEntity entity_target;
	public final Material materialInHand_target;
	public final ArmorSet armorSet_target;
	public final String name_attacker;
	public final String[] groups_target;

	public final ModDamageElement element_attacker;
	public final LivingEntity entity_attacker;
	public final Material materialInHand_attacker;
	public final ArmorSet armorSet_attacker;
	public final String name_target;
	public final String[] groups_attacker;
	
//CONSTRUCTORS
	public DamageEventInfo(LivingEntity eventEntity_target, ModDamageElement eventElement_target, LivingEntity eventEntity_attacker, ModDamageElement eventElement_attacker, RangedElement rangedElement, int eventDamage) 
	{
		this.eventDamage = eventDamage;
		this.rangedElement = rangedElement;
		
		entity_target = eventEntity_target;
		element_target = eventElement_attacker;
		if(entity_target instanceof Player)
		{
			Player player_target = (Player)entity_target;
			materialInHand_target = player_target.getItemInHand().getType();
			armorSet_target = new ArmorSet(player_target);
			name_target = player_target.getName();
			groups_target = (ModDamage.using_Permissions?ModDamage.multigroupPermissions
					?ModDamage.Permissions.getGroups(eventEntity_target.getWorld().getName(), player_target.getName())
					:ModDamage.Permissions.getGroup(eventEntity_target.getWorld().getName(), player_target.getName()).split(" "):emptyStringArray);
		}
		else
		{
			materialInHand_target = null;
			armorSet_target = null;
			name_target = null;
			groups_target = null;
		}

		entity_attacker = eventEntity_attacker;
		element_attacker = eventElement_attacker;
		if(entity_attacker instanceof Player)
		{
			Player player_attacker = (Player)entity_attacker;
			materialInHand_attacker = player_attacker.getItemInHand().getType();
			armorSet_attacker = new ArmorSet(player_attacker);
			name_attacker = player_attacker.getName();
			groups_attacker = (ModDamage.using_Permissions?ModDamage.multigroupPermissions
								?ModDamage.Permissions.getGroups(player_attacker.getWorld().getName(), player_attacker.getName())
								:ModDamage.Permissions.getGroup(player_attacker.getWorld().getName(), player_attacker.getName()).split(" "):emptyStringArray);
		}
		else
		{
			materialInHand_attacker = null;
			armorSet_attacker = null;
			name_attacker = null;
			groups_attacker = null;
		}
		
		world = entity_target.getWorld();
		environment = world.getEnvironment();
	}
}