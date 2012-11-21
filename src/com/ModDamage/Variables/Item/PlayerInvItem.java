package com.ModDamage.Variables.Item;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.Expressions.IntegerExp;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class PlayerInvItem extends DataProvider<ItemStack, Player>
{
	public static final Pattern pattern = Pattern.compile("_(?:iteminslot|invitem|item)_", Pattern.CASE_INSENSITIVE);
	private final IDataProvider<Integer> slot;
	
	public PlayerInvItem(IDataProvider<Player> playerDP, IDataProvider<Integer> slot)
	{
		super(Player.class, playerDP);
		this.slot = slot;
	}

	@Override
	public ItemStack get(Player player, EventData data) throws BailException
	{
		return player.getInventory().getItem(slot.get(data));
	}
	
	@Override
	public Class<ItemStack> provides() { return ItemStack.class; }
	
	public static void register()
	{
		DataProvider.register(ItemStack.class, Player.class, pattern, new IDataParser<ItemStack, Player>()
			{
				@Override
				public IDataProvider<ItemStack> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
				{
					IDataProvider<Integer> slot = IntegerExp.parse(sm, info);
                    if (slot == null) return null;
					
					return new PlayerInvItem(playerDP, slot);
				}
			});
	}
}
