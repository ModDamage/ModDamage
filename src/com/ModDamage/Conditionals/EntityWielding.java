package com.ModDamage.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.StringMatcher;
import com.ModDamage.Alias.ItemAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class EntityWielding extends Conditional<Player> 
{
	public static final Pattern pattern = Pattern.compile("\\.(?:is)?wielding\\.([\\w,@*]+)", Pattern.CASE_INSENSITIVE);
	
	private final Collection<ModDamageItemStack> items;
	
	public EntityWielding(IDataProvider<?> playerDP, Collection<ModDamageItemStack> items)
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
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Entity.class, pattern, new IDataParser<Boolean>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<?> playerDP, Matcher m, StringMatcher sm)
				{
					Collection<ModDamageItemStack> matchedItems = ItemAliaser.match(m.group(1), info);
					if(matchedItems == null || matchedItems.isEmpty()) return null;
					
					return new EntityWielding(playerDP, matchedItems);
				}
			});
	}
}
