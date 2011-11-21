package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;

public class AttackerEventInfo extends ProjectileEventInfo
{	
	public final ModDamageElement element_attacker;
	public final LivingEntity entity_attacker;
	public final Material materialInHand_attacker;
	public final ArmorSet armorSet_attacker;
	public final List<String> groups_attacker;
	
//CONSTRUCTORS
	public AttackerEventInfo(LivingEntity eventEntity_target, ModDamageElement eventElement_target, LivingEntity eventEntity_attacker, ModDamageElement eventElement_attacker, Projectile eventEntity_projectile, ModDamageElement rangedElement, int eventDamage) 
	{
		super(eventEntity_target, eventElement_target, eventEntity_projectile, rangedElement, eventDamage, EventInfoType.ATTACKER);
		
		entity_attacker = eventEntity_attacker;
		element_attacker = eventElement_attacker;
		if(element_attacker.matchesType(ModDamageElement.PLAYER))
		{
			Player player_attacker = (Player)entity_attacker;
			materialInHand_attacker = player_attacker.getItemInHand().getType();
			armorSet_attacker = new ArmorSet(player_attacker);
			groups_attacker = ExternalPluginManager.getPermissionsManager().getGroups(player_attacker);
		}
		else
		{
			this.materialInHand_attacker = element_attacker.matchesType(ModDamageElement.ENDERMAN)?((Enderman)entity_attacker).getCarriedMaterial().getItemType():null;
			armorSet_attacker = null;
			groups_attacker = Arrays.asList();
		}
	}

	protected AttackerEventInfo(LivingEntity eventEntity_attacker, ModDamageElement element_attacker, Material materialInHand_attacker, ArmorSet armorSet_attacker, List<String> groups_attacker, Projectile projectile, ModDamageElement rangedElement, World world, LivingEntity entity_target, ModDamageElement element_target, Material materialInHand_target, ArmorSet armorSet_target, List<String> groups_target, int eventValue)
	{
		super(projectile, rangedElement, world, entity_target, element_target, materialInHand_target, armorSet_target, groups_target, eventValue);
		this.entity_attacker = eventEntity_attacker;
		this.element_attacker = element_attacker;
		this.materialInHand_attacker = materialInHand_attacker;
		this.armorSet_attacker = armorSet_attacker;
		this.groups_attacker = groups_attacker;
	}
	
	@Override
	public AttackerEventInfo clone()
	{
		return new AttackerEventInfo(entity_attacker, element_attacker, materialInHand_attacker, armorSet_attacker, groups_attacker, projectile, rangedElement, world, entity_target, element_target, materialInHand_target, armorSet_target, groups_target, eventValue);
	}
}