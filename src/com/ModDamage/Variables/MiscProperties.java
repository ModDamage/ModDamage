package com.ModDamage.Variables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class MiscProperties
{
	public static void register()
	{
		DataProvider.register(ItemStack.class, Item.class, Pattern.compile("_item", Pattern.CASE_INSENSITIVE),
				new IDataParser<ItemStack, Item>() {
					public IDataProvider<ItemStack> parse(EventInfo info, IDataProvider<Item> locDP, Matcher m, StringMatcher sm) {
						return new DataProvider<ItemStack, Item>(Item.class, locDP) {
								public ItemStack get(Item loc, EventData data) { return loc.getItemStack(); }
								public Class<ItemStack> provides() { return ItemStack.class; }
								public String toString() { return startDP.toString() + "_item"; }
							};
					}
				});
		
		DataProvider.register(Block.class, Location.class, Pattern.compile("_block", Pattern.CASE_INSENSITIVE),
				new IDataParser<Block, Location>() {
					public IDataProvider<Block> parse(EventInfo info, IDataProvider<Location> locDP, Matcher m, StringMatcher sm) {
						return new DataProvider<Block, Location>(Location.class, locDP) {
								public Block get(Location loc, EventData data) { return loc.getBlock(); }
								public Class<Block> provides() { return Block.class; }
								public String toString() { return startDP.toString() + "_block"; }
							};
					}
				});
		
		
		DataProvider.register(Material.class, Block.class, Pattern.compile("_type", Pattern.CASE_INSENSITIVE),
				new IDataParser<Material, Block>() {
					public IDataProvider<Material> parse(EventInfo info, IDataProvider<Block> blockDP, Matcher m, StringMatcher sm) {
						return new DataProvider<Material, Block>(Block.class, blockDP) {
								public Material get(Block block, EventData data) { return block.getType(); }
								public Class<Material> provides() { return Material.class; }
								public String toString() { return startDP.toString() + "_type"; }
							};
					}
				});
	}
}
