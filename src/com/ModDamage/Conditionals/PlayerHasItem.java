package com.ModDamage.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.StringMatcher;
import com.ModDamage.Alias.ItemAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class PlayerHasItem extends Conditional<Player>
{
	public static final Pattern pattern = Pattern.compile("\\.has((?:all)?items|item)\\.([\\w,@*]+)", Pattern.CASE_INSENSITIVE);
	
	private final boolean allItems;
	private final Collection<ModDamageItemStack> items;
	
	public PlayerHasItem(IDataProvider<Player> playerDP, boolean allItems, Collection<ModDamageItemStack> items)
	{
		super(Player.class, playerDP);
		this.allItems = allItems;
		this.items = items;
	}

	@Override
	public Boolean get(Player player, EventData data) throws BailException
	{
		for(ModDamageItemStack item : items)
			item.update(data);
		Inventory inventory = player.getInventory();
		if(allItems)
		{
			outerLoop: for(ModDamageItemStack item : items)
			{
				for (ItemStack stack : inventory)
					if (item.matches(stack))
						continue outerLoop;
				return false;
				//ItemStack temp = item.toItemStack();
				//if(!inventory.contains(temp.getType(), temp.getAmount()))
				//	return false;
			}
			return true;
		}
		else
		{
			for(ModDamageItemStack item : items)
			{
				for (ItemStack stack : inventory)
					if (item.matches(stack))
						return true;
				//ItemStack temp = item.toItemStack();
				//if(inventory.contains(temp.getType(), temp.getAmount()))
				//	return true;
			}
			return false;
		}
	}
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Player.class, pattern, new IDataParser<Boolean, Player>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
				{
					Collection<ModDamageItemStack> items = ItemAliaser.match(m.group(2), info);
					if(items == null || items.isEmpty()) return null;
					
					return new PlayerHasItem(playerDP, m.group(1).equalsIgnoreCase("allitems"), items);
				}
			});
	}
}
