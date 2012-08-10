package com.ModDamage.Variables.Item;

import java.util.regex.Matcher;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class ItemTransformer
{

	public static void register()
	{
		DataProvider.registerTransformer(ItemStack.class, Item.class, new DataProvider.IDataParser<ItemStack, Item>()
			{
				@Override
				public IDataProvider<ItemStack> parse(EventInfo info, Class<?> want, IDataProvider<Item> itemDP, Matcher m, StringMatcher sm)
				{
					return new DataProvider<ItemStack, Item>(Item.class, itemDP)
						{
							@Override
							public ItemStack get(Item start, EventData data) throws BailException
							{
								return start.getItemStack();
							}

							@Override
							public Class<ItemStack> provides()
							{
								return ItemStack.class;
							}
						};
				}
			});
	}

}
