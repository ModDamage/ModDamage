package com.ModDamage.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.Alias.ItemAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class PlayerHasItem extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.has((?:all)?items|item)\\.([\\w,@*]+)", Pattern.CASE_INSENSITIVE);
	
	private final DataRef<Player> playerRef;
	private final boolean allItems;
	private final Collection<ModDamageItemStack> items;
	
	public PlayerHasItem(String configString, DataRef<Player> playerRef, boolean allItems, Collection<ModDamageItemStack> items)
	{
		super(configString);
		this.playerRef = playerRef;
		this.allItems = allItems;
		this.items = items;
	}

	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		Player player = (Player)playerRef.get(data);
		if (player == null) return false;
		
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
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public PlayerHasItem getNew(Matcher matcher, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			DataRef<Player> playerRef = info.get(Player.class, name); if (playerRef == null) return null;
			Collection<ModDamageItemStack> items = ItemAliaser.match(matcher.group(3), info);
			if(items != null && !items.isEmpty())
				return new PlayerHasItem(matcher.group(), playerRef, matcher.group(2).equalsIgnoreCase("allitems"), items);
			return null;
		}
	}
}
