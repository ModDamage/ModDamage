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

public class DynamicItemInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("(\\w+)_("+ 
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
						
						return sm.acceptIf(new DynamicItemInteger(
								entityRef, 
								PlayerItemTarget.valueOf(matcher.group(2).toUpperCase()),
								ItemAttribute.valueOf(matcher.group(3).toUpperCase())));
					}
				});
		DynamicInteger.register(
				Pattern.compile("(\\w+)_("+ 
									 Utils.joinBy("|", ItemAttribute.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						String name = matcher.group(1).toLowerCase();
						DataRef<ItemStack> itemRef = info.get(ItemStack.class, name);
						if (itemRef == null) return null;
						
						return sm.acceptIf(new DynamicItemInteger(
								itemRef, 
								ItemAttribute.valueOf(matcher.group(2).toUpperCase())));
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
	private final DataRef<ItemStack> itemRef;
	private final ItemAttribute itemAttribute;

	public DynamicItemInteger(DataRef<Entity> entityRef, PlayerItemTarget playerItemTarget, ItemAttribute itemAttribute)
	{
		this.entityRef = entityRef;
		this.playerItemTarget = playerItemTarget;
		this.itemAttribute = itemAttribute;
		this.itemRef = null;
	}
	public DynamicItemInteger(DataRef<ItemStack> itemRef, ItemAttribute itemAttribute)
	{
		this.entityRef = null;
		this.playerItemTarget = null;
		this.itemAttribute = itemAttribute;
		this.itemRef = itemRef;
	}

	@Override
	public int getValue(EventData data)
	{
		if (entityRef != null)
		{
			Entity entity = entityRef.get(data);
			if (entity instanceof Player)
				return itemAttribute.getAttribute(playerItemTarget.getItem((Player)entity));
		}
		else
		{
			return itemAttribute.getAttribute(itemRef.get(data));
		}
		return 0;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void setValue(EventData data, int value)
	{
		if (entityRef != null)
		{
			Entity entity = entityRef.get(data);
			if (entity instanceof Player)
			{
				Player player = (Player)entity;
				itemAttribute.setAttribute(playerItemTarget.getItem(player), value);
				player.updateInventory();
			}
		}
		else
		{
			itemAttribute.setAttribute(itemRef.get(data), value);
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
		if (entityRef != null)
			return entityRef + "_" + playerItemTarget.name().toLowerCase() + "_" + itemAttribute.name().toLowerCase();
		return itemRef + "_" + itemAttribute.name().toLowerCase();
	}

}
