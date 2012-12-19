package com.ModDamage.Variables;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataTransformer;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Matchables.EntityType;

public class Transformers
{
	public static void register()
	{
		DataProvider.registerTransformer(Location.class, Entity.class,
				new IDataTransformer<Location, Entity>() {
					public IDataProvider<Location> transform(EventInfo info, IDataProvider<Entity> entityDP) {
						return new DataProvider<Location, Entity>(Entity.class, entityDP) {
								public Location get(Entity entity, EventData data) { return entity.getLocation(); }
								public Class<Location> provides() { return Location.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});
		
		DataProvider.registerTransformer(EntityType.class, Entity.class,
				new IDataTransformer<EntityType, Entity>() {
					public IDataProvider<EntityType> transform(EventInfo info, final IDataProvider<Entity> entityDP) {
						return new IDataProvider<EntityType>() {
								public EntityType get(EventData data) throws BailException { return EntityType.get(entityDP.get(data)); }
								public Class<EntityType> provides() { return EntityType.class; }
								public String toString() { return entityDP.toString(); }
							};
					}
				});
		
		
		
		DataProvider.registerTransformer(Location.class, Block.class,
				new IDataTransformer<Location, Block>() {
					public IDataProvider<Location> transform(EventInfo info, IDataProvider<Block> blockDP) {
						return new DataProvider<Location, Block>(Block.class, blockDP) {
								public Location get(Block block, EventData data) { return block.getLocation(); }
								public Class<Location> provides() { return Location.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});

		
		DataProvider.registerTransformer(Material.class, Block.class,
				new IDataTransformer<Material, Block>() {
					public IDataProvider<Material> transform(EventInfo info, IDataProvider<Block> blockDP) {
						return new DataProvider<Material, Block>(Block.class, blockDP) {
								public Material get(Block block, EventData data) { return block.getType(); }
								public Class<Material> provides() { return Material.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});

		DataProvider.registerTransformer(Integer.class, Number.class,
				new IDataTransformer<Integer, Number>() {
					public IDataProvider<Integer> transform(EventInfo info, IDataProvider<Number> numDP) {
						return new DataProvider<Integer, Number>(Number.class, numDP) {
								public Integer get(Number num, EventData data) { return num.intValue(); }
								public Class<Integer> provides() { return Integer.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});


		DataProvider.registerTransformer(EntityEquipment.class, LivingEntity.class,
				new IDataTransformer<EntityEquipment, LivingEntity>() {
					public IDataProvider<EntityEquipment> transform(EventInfo info, IDataProvider<LivingEntity> entityDP) {
						return new DataProvider<EntityEquipment, LivingEntity>(LivingEntity.class, entityDP) {
								public EntityEquipment get(LivingEntity entity, EventData data) { return entity.getEquipment(); }
								public Class<EntityEquipment> provides() { return EntityEquipment.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});
		

//		DataProvider.registerTransformer(ItemStack.class, Item.class,
//				new IDataTransformer<ItemStack, Item>() {
//					public IDataProvider<ItemStack> transform(EventInfo info, IDataProvider<Item> itemDP) {
//						return new DataProvider<ItemStack, Item>(Item.class, itemDP) {
//								public ItemStack get(Item start, EventData data) { return start.getItemStack(); }
//								public Class<ItemStack> provides() { return ItemStack.class; }
//								public String toString() { return startDP.toString(); }
//							};
//					}
//			});
		
		
		DataProvider.registerTransformer(Material.class, ItemHolder.class,
				new IDataTransformer<Material, ItemHolder>() {
					public IDataProvider<Material> transform(EventInfo info, IDataProvider<ItemHolder> itemDP) {
						return new DataProvider<Material, ItemHolder>(ItemHolder.class, itemDP) {
								public Material get(ItemHolder item, EventData data) { return item.getType(); }
								public Class<Material> provides() { return Material.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});
		
		
		
		
		DataProvider.registerTransformer(Integer.class, Material.class,
				new IDataTransformer<Integer, Material>() {
					public IDataProvider<Integer> transform(EventInfo info, IDataProvider<Material> materialDP) {
						return new DataProvider<Integer, Material>(Material.class, materialDP) {
								public Integer get(Material material, EventData data) { return material.getId(); }
								public Class<Integer> provides() { return Integer.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});
		

		
		DataProvider.registerTransformer(String.class, Object.class,
				new IDataTransformer<String, Object>() {
					public IDataProvider<String> transform(EventInfo info, IDataProvider<Object> objDP) {
						return new DataProvider<String, Object>(Object.class, objDP) {
								public String get(Object obj, EventData data) { return obj.toString(); }
								public Class<String> provides() { return String.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});

	}
}
