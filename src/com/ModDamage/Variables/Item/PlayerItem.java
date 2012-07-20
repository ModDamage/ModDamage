package com.ModDamage.Variables.Item;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class PlayerItem extends DataProvider<ItemStack, Player>
{
	public static void register()
	{
		DataProvider.register(ItemStack.class, Player.class, 
				Pattern.compile("_("+Utils.joinBy("|", PlayerItemTarget.values()) +")", Pattern.CASE_INSENSITIVE),
				new IDataParser<ItemStack, Player>()
				{
					@Override
					public IDataProvider<ItemStack> parse(EventInfo info, Class<?> want, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
					{
						return sm.acceptIf(new PlayerItem(
								playerDP, 
								PlayerItemTarget.valueOf(m.group(1).toUpperCase())));
					}
				});
	}
	
	enum PlayerItemTarget {
		WIELDED {
			public ItemStack getItem(Player player) {
				return player.getItemInHand();
			}
		},
		HELMET {
			public ItemStack getItem(Player player) {
				return player.getInventory().getHelmet();
			}
		},
		CHESTPLATE {
			public ItemStack getItem(Player player) {
				return player.getInventory().getChestplate();
			}
		},
		LEGGINGS {
			public ItemStack getItem(Player player) {
				return player.getInventory().getLeggings();
			}
		},
		BOOTS {
			public ItemStack getItem(Player player) {
				return player.getInventory().getBoots();
			}
		};
		
		public abstract ItemStack getItem(Player player);
	}
	

	private final PlayerItemTarget playerItemTarget;

	public PlayerItem(IDataProvider<Player> playerDP, PlayerItemTarget playerItemTarget)
	{
		super(Player.class, playerDP);
		this.playerItemTarget = playerItemTarget;
	}

	@Override
	public ItemStack get(Player player, EventData data) throws BailException
	{
		return playerItemTarget.getItem(player);
	}

	@Override
	public Class<ItemStack> provides() { return ItemStack.class; }
	
	@Override
	public String toString()
	{
		return startDP + "_" + playerItemTarget.name().toLowerCase();
	}
}
