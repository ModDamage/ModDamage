package com.ModDamage.Variables.Item;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataTransformer;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class ItemTransformer
{

	public static void register()
	{
		DataProvider.registerTransformer(ItemStack.class, Item.class,
				new IDataTransformer<ItemStack, Item>() {
					public IDataProvider<ItemStack> transform(EventInfo info, IDataProvider<Item> itemDP) {
						return new DataProvider<ItemStack, Item>(Item.class, itemDP) {
								public ItemStack get(Item start, EventData data) { return start.getItemStack(); }
								public Class<ItemStack> provides() { return ItemStack.class; }
								public String toString() { return startDP.toString(); }
							};
					}
			});
		
		DataProvider.registerTransformer(Material.class, Item.class,
				new IDataTransformer<Material, Item>() {
					public IDataProvider<Material> transform(EventInfo info, IDataProvider<Item> blockDP) {
						return new DataProvider<Material, Item>(Item.class, blockDP) {
								public Material get(Item block, EventData data) { return block.getItemStack().getType(); }
								public Class<Material> provides() { return Material.class; }
								public String toString() { return startDP.toString(); }
							};
					}
				});
	}

}
