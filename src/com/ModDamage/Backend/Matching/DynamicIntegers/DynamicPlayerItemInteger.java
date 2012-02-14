package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class DynamicPlayerItemInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("([a-z]+)_("+ 
									 Utils.joinBy("|", PlayerItemTarget.values()) +")_("+ 
									 Utils.joinBy("|", ItemAttribute.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						String name = matcher.group(1).toLowerCase();
						DataRef<Entity> entityRef = info.get(Entity.class, name);
						if (entityRef == null) return null;
						
						return sm.acceptIf(new DynamicPlayerItemInteger(
								entityRef, 
								PlayerItemTarget.valueOf(matcher.group(2).toUpperCase()),
								ItemAttribute.valueOf(matcher.group(3).toUpperCase())));
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
	
	enum ItemAttribute {
		DURABILITY(true) {
			public int getAttribute(ItemStack item) {
				return item.getDurability();
			}
			public void setAttribute(ItemStack item, int attr) {
				item.setDurability((short) attr);
			}
		},
		DATA(true) {
			public int getAttribute(ItemStack item) {
				return item.getData().getData();
			}
			public void setAttribute(ItemStack item, int attr) {
				item.getData().setData((byte) attr);
			}
		},
		AMOUNT(true) {
			public int getAttribute(ItemStack item) {
				return item.getAmount();
			}
			public void setAttribute(ItemStack item, int attr) {
				item.setAmount(attr);
			}
		},
		MAX_AMOUNT {
			public int getAttribute(ItemStack item) {
				return item.getMaxStackSize();
			}
		},
		TYPE(true) {
			public int getAttribute(ItemStack item) {
				return item.getTypeId();
			}
			public void setAttribute(ItemStack item, int attr) {
				item.setTypeId(attr);
			}
		},
		MAX_DURABILITY {
			public int getAttribute(ItemStack item) {
				return ((CraftItemStack) item).getHandle().getItem().getMaxDurability();
			}
		};
		
		boolean settable = false;
		private ItemAttribute() {}
		private ItemAttribute(boolean settable) { this.settable = settable; }
		public abstract int getAttribute(ItemStack item);
		public void setAttribute(ItemStack item, int attr){}
		
	}
	
	private final DataRef<Entity> entityRef;
	private final PlayerItemTarget playerItemTarget;
	private final ItemAttribute itemAttribute;

	public DynamicPlayerItemInteger(DataRef<Entity> entityRef, PlayerItemTarget playerItemTarget, ItemAttribute itemAttribute)
	{
		this.entityRef = entityRef;
		this.playerItemTarget = playerItemTarget;
		this.itemAttribute = itemAttribute;
	}

	@Override
	public int getValue(EventData data)
	{
		Entity entity = entityRef.get(data);
		if (entity instanceof Player)
			return itemAttribute.getAttribute(playerItemTarget.getItem((Player)entity));
		return 0;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void setValue(EventData data, int value)
	{
		Entity entity = entityRef.get(data);
		if (entity instanceof Player)
		{
			Player player = (Player)entity;
			itemAttribute.setAttribute(playerItemTarget.getItem(player), value);
			player.updateInventory();
		}
	}
	
	@Override
	public boolean isSettable()
	{
		return itemAttribute.settable;
	}

	@Override
	public String toString()
	{
		return /*FIXME: entityReference.name() +*/ "_" + playerItemTarget.name().toLowerCase() + "_" + itemAttribute.name().toLowerCase();
	}

}
