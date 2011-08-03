package com.KoryuObihiro.bukkit.ModDamage.Backend;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

@SuppressWarnings("deprecation")
public class AttackerEventInfo extends TargetEventInfo
{	
	public final ModDamageElement element_attacker;
	public final LivingEntity entity_attacker;
	public final Material materialInHand_attacker;
	public final ArmorSet armorSet_attacker;
	public final String name_attacker;
	public final String[] groups_attacker;
	
//CONSTRUCTORS
	public AttackerEventInfo(LivingEntity eventEntity_target, ModDamageElement eventElement_target, LivingEntity eventEntity_attacker, ModDamageElement eventElement_attacker, RangedElement rangedElement, int eventDamage) 
	{
		super(eventEntity_target, eventElement_target, eventDamage, rangedElement);

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
	}
}