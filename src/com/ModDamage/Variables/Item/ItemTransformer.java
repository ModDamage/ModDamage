package com.ModDamage.Variables.Item;

import java.util.regex.Matcher;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class ItemTransformer
{

	public static void register()
	{
		DataProvider.registerTransformer(ItemStack.class, Item.class,
				new DataProvider.IDataParser<ItemStack, Item>() {
					public IDataProvider<ItemStack> parse(EventInfo info, Class<?> want, IDataProvider<Item> itemDP, Matcher m, StringMatcher sm) {
						return new DataProvider<ItemStack, Item>(Item.class, itemDP) {
								public ItemStack get(Item start, EventData data) { return start.getItemStack(); }
								public Class<ItemStack> provides() { return ItemStack.class; }
								public String toString() { return startDP.toString(); }
							};
					}
			});
		
		DataProvider.registerTransformer(Material.class, Item.class,
				new IDataParser<Material, Item>() {
					public IDataProvider<Material> parse(EventInfo info, Class<?> want, IDataProvider<Item> blockDP, Matcher m, StringMatcher sm) {
						return new DataProvider<Material, Item>(Item.class, blockDP) {
								public Material get(Item block, EventData data) { return block.getItemStack().getType(); }
								public Class<Material> provides() { return Material.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});
	}

}
