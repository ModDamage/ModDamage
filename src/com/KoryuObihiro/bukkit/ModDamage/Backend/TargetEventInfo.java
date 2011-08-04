package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

@SuppressWarnings("deprecation")
public class TargetEventInfo
{
	String[] emptyStringArray = {};
	
	public static final Logger log = ModDamage.log;
	public static final Server server = ModDamage.server;
	
	public int eventValue;
	public final World world;
	public final Environment environment;
	
	public final ModDamageElement element_target;
	public final LivingEntity entity_target;
	public final Material materialInHand_target;
	public final ArmorSet armorSet_target;
	public final String name_target;
	public final List<String> groups_target;
	
//CONSTRUCTORS	
	public TargetEventInfo(LivingEntity entity, ModDamageElement eventElement_target, int eventValue) 
	{
		this.eventValue = eventValue;
		this.entity_target = entity;
		this.element_target = eventElement_target;
		if(entity instanceof Player)
		{
			Player player_target = (Player)entity;
			materialInHand_target = player_target.getItemInHand().getType();
			armorSet_target = new ArmorSet(player_target);
			name_target = player_target.getName();
			groups_target = Arrays.asList(ModDamage.using_Permissions?ModDamage.multigroupPermissions
					?ModDamage.Permissions.getGroups(entity.getWorld().getName(), player_target.getName())
					:ModDamage.Permissions.getGroup(entity.getWorld().getName(), player_target.getName()).split(" "):emptyStringArray);
		}
		else
		{
			materialInHand_target = null;
			armorSet_target = null;
			name_target = null;
			groups_target = null;
		}
		
		world = entity.getWorld();	
		environment = world.getEnvironment();
	}
}