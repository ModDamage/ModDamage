package com.ModDamage.Variables;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataTransformer;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Matchables.EntityType;

public class Transformers
{
	@SuppressWarnings("rawtypes")
	public static void register()
	{
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
		
		DataProvider.registerTransformer(Block.class, Location.class,
				new IDataTransformer<Block, Location>() {
					public IDataProvider<Block> transform(EventInfo info, IDataProvider<Location> locDP) {
						return new DataProvider<Block, Location>(Location.class, locDP) {
								public Block get(Location loc, EventData data) { return loc.getBlock(); }
								public Class<Block> provides() { return Block.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});
		
		DataProvider.registerTransformer(Enum.class, Entity.class,
				new IDataTransformer<Enum, Entity>() {
					public IDataProvider<Enum> transform(EventInfo info, final IDataProvider<Entity> entityDP) {
						return new IDataProvider<Enum>() {
								public EntityType get(EventData data) throws BailException { return EntityType.get(entityDP.get(data)); }
								@SuppressWarnings("unchecked")
								public Class provides() { return EntityType.class; }
								public String toString() { return entityDP.toString(); }
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

	}
}
