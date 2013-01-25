package com.ModDamage.Properties;

import org.bukkit.inventory.EntityEquipment;

import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.Parsing.Property.Properties;
import com.ModDamage.Parsing.Property.SettableProperty;

public class EquipmentProps
{
	public static void register()
	{
		Properties.register(new SettableProperty<ItemHolder, EntityEquipment>("held", ItemHolder.class, EntityEquipment.class) {
			public void set(EntityEquipment equipment, EventData data, ItemHolder holder) { equipment.setItemInHand(holder.getItem()); }

			public ItemHolder get(final EntityEquipment equipment, EventData data) {
				return new ItemHolder(equipment.getItemInHand()) {
					public void save() { equipment.setItemInHand(getItem()); }
				};
			}
		});
		Properties.register("held_dropchance", EntityEquipment.class, "getItemInHandDropChance", "setItemInHandDropChance");
    
	    Properties.register(new SettableProperty<ItemHolder, EntityEquipment>("wielded", ItemHolder.class, EntityEquipment.class) {
				public void set(EntityEquipment equipment, EventData data, ItemHolder holder) { equipment.setItemInHand(holder.getItem()); }
	
				public ItemHolder get(final EntityEquipment equipment, EventData data) {
					return new ItemHolder(equipment.getItemInHand()) {
						public void save() { equipment.setItemInHand(getItem()); }
					};
				}
			});
		Properties.register("wielded_dropchance", EntityEquipment.class, "getItemInHandDropChance", "setItemInHandDropChance");
	    
	    Properties.register(new SettableProperty<ItemHolder, EntityEquipment>("helmet", ItemHolder.class, EntityEquipment.class) {
				public void set(EntityEquipment equipment, EventData data, ItemHolder holder) { equipment.setHelmet(holder.getItem()); }
	
				public ItemHolder get(final EntityEquipment equipment, EventData data) {
					return new ItemHolder(equipment.getHelmet()) {
						public void save() { equipment.setHelmet(getItem()); }
					};
				}
			});
		Properties.register("helmet_dropchance", EntityEquipment.class, "getHelmetDropChance", "setHelmetDropChance");
	    
	    Properties.register(new SettableProperty<ItemHolder, EntityEquipment>("chestplate", ItemHolder.class, EntityEquipment.class) {
				public void set(EntityEquipment equipment, EventData data, ItemHolder holder) { equipment.setChestplate(holder.getItem()); }
	
				public ItemHolder get(final EntityEquipment equipment, EventData data) {
					return new ItemHolder(equipment.getChestplate()) {
						public void save() { equipment.setChestplate(getItem()); }
					};
				}
			});
		Properties.register("chestplate_dropchance", EntityEquipment.class, "getChestplateDropChance", "setChestplateDropChance");
	    
	    Properties.register(new SettableProperty<ItemHolder, EntityEquipment>("leggings", ItemHolder.class, EntityEquipment.class) {
				public void set(EntityEquipment equipment, EventData data, ItemHolder holder) { equipment.setLeggings(holder.getItem()); }
	
				public ItemHolder get(final EntityEquipment equipment, EventData data) {
					return new ItemHolder(equipment.getLeggings()) {
						public void save() { equipment.setLeggings(getItem()); }
					};
				}
			});
		Properties.register("leggings_dropchance", EntityEquipment.class, "getLeggingsDropChance", "setLeggingsDropChance");
	    
	    Properties.register(new SettableProperty<ItemHolder, EntityEquipment>("boots", ItemHolder.class, EntityEquipment.class) {
				public void set(EntityEquipment equipment, EventData data, ItemHolder holder) { equipment.setBoots(holder.getItem()); }
	
				public ItemHolder get(final EntityEquipment equipment, EventData data) {
					return new ItemHolder(equipment.getBoots()) {
						public void save() { equipment.setBoots(getItem()); }
					};
				}
			});
		Properties.register("boots_dropchance", EntityEquipment.class, "getBootsDropChance", "setBootsDropChance");
	}
}
