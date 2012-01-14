package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

public class ItemEventInfo extends AttackerEventInfo
{

	public ItemEventInfo(LivingEntity eventEntity_target, ModDamageElement eventElement_target,
			LivingEntity eventEntity_attacker, ModDamageElement eventElement_attacker,
			Projectile eventEntity_projectile, ModDamageElement rangedElement, int eventDamage)
	{
		super(eventEntity_target, eventElement_target, eventEntity_attacker, eventElement_attacker,
				eventEntity_projectile, rangedElement, eventDamage);
	}

	public ItemEventInfo(LivingEntity eventEntity_attacker, ModDamageElement element_attacker,
			Material materialInHand_attacker, ArmorSet armorSet_attacker, List<String> groups_attacker,
			Projectile projectile, ModDamageElement rangedElement, World world, LivingEntity entity_target,
			ModDamageElement element_target, Material materialInHand_target, ArmorSet armorSet_target,
			List<String> groups_target, int eventValue)
	{
		super(eventEntity_attacker, element_attacker, materialInHand_attacker, armorSet_attacker, groups_attacker,
				projectile, rangedElement, world, entity_target, element_target, materialInHand_target,
				armorSet_target, groups_target, eventValue);
	}
	
	public ItemEventInfo(AttackerEventInfo eventInfo)
	{
		super(eventInfo.entity_attacker, eventInfo.element_attacker, eventInfo.materialInHand_attacker, eventInfo.armorSet_attacker, eventInfo.groups_attacker,
				eventInfo.projectile, eventInfo.rangedElement, eventInfo.world, eventInfo.entity_target, eventInfo.element_target, eventInfo.materialInHand_target,
				eventInfo.armorSet_target, eventInfo.groups_target, eventInfo.eventValue);
	}

}
