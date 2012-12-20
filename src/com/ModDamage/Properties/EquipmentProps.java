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
					public void eventFinished(boolean success) { equipment.setItemInHand(getItem()); }
				};
			}
		});
    
	    Properties.register(new SettableProperty<ItemHolder, EntityEquipment>("wielded", ItemHolder.class, EntityEquipment.class) {
				public void set(EntityEquipment equipment, EventData data, ItemHolder holder) { equipment.setItemInHand(holder.getItem()); }
	
				public ItemHolder get(final EntityEquipment equipment, EventData data) {
					return new ItemHolder(equipment.getItemInHand()) {
						public void eventFinished(boolean success) { equipment.setItemInHand(getItem()); }
					};
				}
			});
	    
	    Properties.register(new SettableProperty<ItemHolder, EntityEquipment>("helmet", ItemHolder.class, EntityEquipment.class) {
				public void set(EntityEquipment equipment, EventData data, ItemHolder holder) { equipment.setHelmet(holder.getItem()); }
	
				public ItemHolder get(final EntityEquipment equipment, EventData data) {
					return new ItemHolder(equipment.getHelmet()) {
						public void eventFinished(boolean success) { equipment.setHelmet(getItem()); }
					};
				}
			});
	    
	    Properties.register(new SettableProperty<ItemHolder, EntityEquipment>("chestplate", ItemHolder.class, EntityEquipment.class) {
				public void set(EntityEquipment equipment, EventData data, ItemHolder holder) { equipment.setChestplate(holder.getItem()); }
	
				public ItemHolder get(final EntityEquipment equipment, EventData data) {
					return new ItemHolder(equipment.getChestplate()) {
						public void eventFinished(boolean success) { equipment.setChestplate(getItem()); }
					};
				}
			});
	    
	    Properties.register(new SettableProperty<ItemHolder, EntityEquipment>("leggings", ItemHolder.class, EntityEquipment.class) {
				public void set(EntityEquipment equipment, EventData data, ItemHolder holder) { equipment.setLeggings(holder.getItem()); }
	
				public ItemHolder get(final EntityEquipment equipment, EventData data) {
					return new ItemHolder(equipment.getLeggings()) {
						public void eventFinished(boolean success) { equipment.setLeggings(getItem()); }
					};
				}
			});
	    
	    Properties.register(new SettableProperty<ItemHolder, EntityEquipment>("boots", ItemHolder.class, EntityEquipment.class) {
				public void set(EntityEquipment equipment, EventData data, ItemHolder holder) { equipment.setBoots(holder.getItem()); }
	
				public ItemHolder get(final EntityEquipment equipment, EventData data) {
					return new ItemHolder(equipment.getBoots()) {
						public void eventFinished(boolean success) { equipment.setBoots(getItem()); }
					};
				}
			});
	}
}
