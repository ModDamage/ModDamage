package com.ModDamage.Conditionals;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Alias.ItemAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class PlayerHasItem extends Conditional<Player>
{
	public static final Pattern pattern = Pattern.compile("\\.has(?:((?:all)?items)|anyitem)\\.([\\w,@*]+)", Pattern.CASE_INSENSITIVE);
	
	private final boolean allItems;
	private final List<ModDamageItemStack> items;
	
	public PlayerHasItem(IDataProvider<Player> playerDP, boolean allItems, List<ModDamageItemStack> items)
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
		int[] counts = new int[items.size()];
		
		outerLoop: for (ItemStack stack : inventory)
		{
			int i = -1;
			for(ModDamageItemStack item : items)
			{
				i++;
				if (item.typeMatches(stack))
				{
					counts[i] += stack.getAmount();
					continue outerLoop;
				}
			}
		}
		
		
		if(allItems)
		{
			for (int i = 0; i < counts.length; i++)
			{
				if (counts[i] < items.get(i).getAmount())
					return false;
			}
			return true;
		}
		else
		{
			for (int i = 0; i < counts.length; i++)
			{
				if (counts[i] >= items.get(i).getAmount())
					return true;
			}
			return false;
		}
	}
	
	@Override
	public String toString()
	{
		return startDP + ".has"+(allItems? "all":"")+"items." + Utils.joinBy(",", items);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Player.class, pattern, new IDataParser<Boolean, Player>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
				{
					List<ModDamageItemStack> items = ItemAliaser.match(m.group(2), info);
					if(items == null || items.isEmpty()) return null;
					
					return new PlayerHasItem(playerDP, m.group(1) != null, items);
				}
			});
	}
}
