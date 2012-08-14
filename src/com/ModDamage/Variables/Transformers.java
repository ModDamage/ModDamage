package com.ModDamage.Variables;

import java.util.regex.Matcher;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class Transformers
{
	public static void register()
	{
		DataProvider.registerTransformer(Location.class, Block.class,
				new IDataParser<Location, Block>() {
					public IDataProvider<Location> parse(EventInfo info, Class<?> want, IDataProvider<Block> blockDP, Matcher m, StringMatcher sm) {
						return new DataProvider<Location, Block>(Block.class, blockDP) {
								public Location get(Block block, EventData data) { return block.getLocation(); }
								public Class<Location> provides() { return Location.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});
		
		DataProvider.registerTransformer(Location.class, Entity.class,
				new IDataParser<Location, Entity>() {
					public IDataProvider<Location> parse(EventInfo info, Class<?> want, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm) {
						return new DataProvider<Location, Entity>(Entity.class, entityDP) {
								public Location get(Entity entity, EventData data) { return entity.getLocation(); }
								public Class<Location> provides() { return Location.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});
		
		DataProvider.registerTransformer(Block.class, Location.class,
				new IDataParser<Block, Location>() {
					public IDataProvider<Block> parse(EventInfo info, Class<?> want, IDataProvider<Location> locDP, Matcher m, StringMatcher sm) {
						return new DataProvider<Block, Location>(Location.class, locDP) {
								public Block get(Location loc, EventData data) { return loc.getBlock(); }
								public Class<Block> provides() { return Block.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});
	}
}
