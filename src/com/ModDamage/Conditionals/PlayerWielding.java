package com.ModDamage.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Alias.ItemAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;

public class PlayerWielding extends Conditional<Player> 
{
	public static final Pattern pattern = Pattern.compile("\\.(?:is)?wielding\\.([\\w,@*]+)", Pattern.CASE_INSENSITIVE);
	
	private final Collection<ModDamageItemStack> items;
	
	public PlayerWielding(IDataProvider<Player> playerDP, Collection<ModDamageItemStack> items)
	{
		super(Player.class, playerDP);
		this.items = items;
	}
	@Override
	public Boolean get(Player player, EventData data) throws BailException
	{
		ItemStack wieldedItem = player.getItemInHand();
		for (ModDamageItemStack item : items)
		{
			item.update(data);
			if (item.matches(wieldedItem))
				return true;
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return startDP + ".iswielding." + Utils.joinBy(",", items);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Player.class, pattern, new IDataParser<Boolean, Player>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
				{
					Collection<ModDamageItemStack> matchedItems = ItemAliaser.match(m.group(1), info);
					if(matchedItems == null || matchedItems.isEmpty()) return null;
					
					return new PlayerWielding(playerDP, matchedItems);
				}
			});
	}
}
