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
import com.ModDamage.Expressions.IntegerExp;

public class PlayerItemInSlotMatches extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.iteminslot\\.([^.]*?)\\.matches\\.([\\w,@*]+)", Pattern.CASE_INSENSITIVE);
	private final DataRef<Player> playerRef;
	private final IntegerExp slot;
	private final Collection<ModDamageItemStack> items;
	
	public PlayerItemInSlotMatches(String configString, DataRef<Player> playerRef, IntegerExp slot, Collection<ModDamageItemStack> items)
	{
		super(configString);
		this.playerRef = playerRef;
		this.slot = slot;
		this.items = items;
	}

	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		Player player = playerRef.get(data);
		if (player == null) return false;
		
		Inventory inventory = ((Player)playerRef.get(data)).getInventory();
		ItemStack stack = inventory.getItem(slot.getValue(data));
		for (ModDamageItemStack item : items)
		{
			item.update(data);
			if (item.matches(stack))
				return true;
		}
		
		return false;
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public PlayerItemInSlotMatches getNew(Matcher matcher, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			DataRef<Player> playerRef = info.get(Player.class, name); if (playerRef == null) return null;
			IntegerExp slot = IntegerExp.getNew(matcher.group(2), info); if (slot == null) return null;
			Collection<ModDamageItemStack> items = ItemAliaser.match(matcher.group(3), info); if (items == null || items.isEmpty()) return null;
			
			return new PlayerItemInSlotMatches(matcher.group(), playerRef, slot, items);
		}
	}
}
