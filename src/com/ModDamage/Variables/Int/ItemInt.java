package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.SettableIntegerExp;

public class ItemInt extends SettableIntegerExp<ItemStack>
{
	public static void register()
	{
		DataProvider.register(Integer.class, ItemStack.class, 
				Pattern.compile("_("+Utils.joinBy("|", ItemProperty.values()) +")", Pattern.CASE_INSENSITIVE),
				new IDataParser<Integer>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, IDataProvider<?> itemDP, Matcher m, StringMatcher sm)
					{
						return sm.acceptIf(new ItemInt(
								itemDP, 
								ItemProperty.valueOf(m.group(1).toUpperCase())));
					}
				});
	}
	
	enum ItemProperty {
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
		private ItemProperty() {}
		private ItemProperty(boolean settable) { this.settable = settable; }
		public abstract int getAttribute(ItemStack item);
		public void setAttribute(ItemStack item, int attr){}
		
	}
	
	private final ItemProperty itemAttribute;

	public ItemInt(IDataProvider<?> itemDP, ItemProperty itemAttribute)
	{
		super(ItemStack.class, itemDP);
		this.itemAttribute = itemAttribute;
	}

	@Override
	public Integer myGet(ItemStack item, EventData data) throws BailException
	{
		return itemAttribute.getAttribute(item);
	}
	
	@Override
	public void mySet(ItemStack item, EventData data, Integer value)
	{
		itemAttribute.setAttribute(item, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return itemAttribute.settable;
	}

	@Override
	public String toString()
	{
		return startDP + "_" + itemAttribute.name().toLowerCase();
	}

}
