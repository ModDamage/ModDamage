package com.ModDamage.Variables;

import java.util.regex.Matcher;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class LocationBlock
{
	public static void register()
	{
		DataProvider.registerTransformer(Block.class, Location.class,
				new IDataParser<Block, Location>() {
					public IDataProvider<Block> parse(EventInfo info, Class<?> want, IDataProvider<Location> locDP, Matcher m, StringMatcher sm) {
						return new DataProvider<Block, Location>(Location.class, locDP) {
								public Block get(Location loc, EventData data) { return loc.getBlock(); }
								public Class<Block> provides() { return Block.class; }
							};
					}
				});
	}
}
