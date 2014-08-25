package com.moddamage.variables;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.moddamage.backend.ItemHolder;
import com.moddamage.eventinfo.EventData;
import com.moddamage.matchables.EntityType;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.property.Property;
import com.moddamage.parsing.property.PropertyTransformer;

public class Transformers
{
	public static void register()
	{
		DataProvider.registerTransformer(Entity.class, "getLocation");
		
		DataProvider.registerTransformer(EntityType.class, Entity.class, new PropertyTransformer<EntityType, Entity>(
				new Property<EntityType, Entity>("@transformer", EntityType.class, Entity.class) {
					public EntityType get(Entity entity, EventData data) {
						return EntityType.get(entity);
					}
				}));
		
		DataProvider.registerTransformer(UUID.class, String.class, new PropertyTransformer<UUID, String>(
				new Property<UUID, String>("@transformer", UUID.class, String.class) {
					public UUID get(String start, EventData data) {
						return UUID.fromString(start);
					}
				}));
		
		DataProvider.registerTransformer(Block.class, "getLocation");

		DataProvider.registerTransformer(Block.class, "getType");

		DataProvider.registerTransformer(Number.class, "intValue");
		DataProvider.registerTransformer(Number.class, "byteValue");
		DataProvider.registerTransformer(Number.class, "longValue");
		DataProvider.registerTransformer(Number.class, "shortValue");
		DataProvider.registerTransformer(Number.class, "floatValue");
		DataProvider.registerTransformer(Number.class, "doubleValue");

		DataProvider.registerTransformer(LivingEntity.class, "getEquipment");
		
		DataProvider.registerTransformer(ItemHolder.class, "getType");
		
		DataProvider.registerTransformer(Material.class, "getId");
		
		DataProvider.registerTransformer(Object.class, "toString");
	}
}
