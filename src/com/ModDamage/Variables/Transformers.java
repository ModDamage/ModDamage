package com.ModDamage.Variables;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.Matchables.EntityType;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.Property.Property;
import com.ModDamage.Parsing.Property.PropertyTransformer;

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
