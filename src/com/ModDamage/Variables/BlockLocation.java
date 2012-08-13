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

public class BlockLocation
{
	public static void register()
	{
		DataProvider.registerTransformer(Location.class, Block.class,
				new IDataParser<Location, Block>() {
					public IDataProvider<Location> parse(EventInfo info, Class<?> want, IDataProvider<Block> blockDP, Matcher m, StringMatcher sm) {
						return new DataProvider<Location, Block>(Block.class, blockDP) {
								public Location get(Block block, EventData data) { return block.getLocation(); }
								public Class<Location> provides() { return Location.class; }
							};
					}
				});
	}
}
