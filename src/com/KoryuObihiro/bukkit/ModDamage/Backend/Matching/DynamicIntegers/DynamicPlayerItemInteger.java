package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.Utils;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;

public class DynamicPlayerItemInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("("+ Utils.joinBy("|", EntityReference.values()) +")_("+ 
									 Utils.joinBy("|", PlayerItemTarget.values()) +")_("+ 
									 Utils.joinBy("|", ItemAttribute.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DIResult getNewFromFront(Matcher matcher, String rest)
					{
						return new DIResult(new DynamicPlayerItemInteger(
								EntityReference.valueOf(matcher.group(1).toUpperCase()), 
								PlayerItemTarget.valueOf(matcher.group(2).toUpperCase()),
								ItemAttribute.valueOf(matcher.group(3).toUpperCase())), rest);
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
	
	EntityReference entityReference;
	PlayerItemTarget playerItemTarget;
	ItemAttribute itemAttribute;

	public DynamicPlayerItemInteger(EntityReference entityReference, PlayerItemTarget playerItemTarget, ItemAttribute itemAttribute)
	{
		this.entityReference = entityReference;
		this.playerItemTarget = playerItemTarget;
		this.itemAttribute = itemAttribute;
	}

	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		Entity entity = entityReference.getEntity(eventInfo);
		if (entity instanceof Player)
			return itemAttribute.getAttribute(playerItemTarget.getItem((Player)entity));
		return 0;
	}
	
	@Override
	public void setValue(TargetEventInfo eventInfo, int value)
	{
		Entity entity = entityReference.getEntity(eventInfo);
		if (entity instanceof Player)
			itemAttribute.setAttribute(playerItemTarget.getItem((Player)entity), value);
	}
	
	@Override
	public boolean isSettable()
	{
		return itemAttribute.settable;
	}

}
