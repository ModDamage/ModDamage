package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EntityType;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.Backend.Aliasing.ItemAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class PlayerItemInSlotMatches extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.iteminslot\\.([^.]*?)\\.matches\\.([\\w,@*]+)", Pattern.CASE_INSENSITIVE);
	private final DataRef<Entity> entityRef;
	private final DataRef<EntityType> entityElementRef;
	private final DynamicInteger slot;
	private final Collection<ModDamageItemStack> items;
	
	public PlayerItemInSlotMatches(String configString, DataRef<Entity> entityRef, DataRef<EntityType> entityElementRef, DynamicInteger slot, Collection<ModDamageItemStack> items)
	{
		super(configString);
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.slot = slot;
		this.items = items;
	}

	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		if(entityElementRef.get(data).matches(EntityType.PLAYER))
		{
			Inventory inventory = ((Player)entityRef.get(data)).getInventory();
			ItemStack stack = inventory.getItem(slot.getValue(data));
			for (ModDamageItemStack item : items)
			{
				item.update(data);
				if (item.matches(stack))
					return true;
			}
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
			DataRef<Entity> entityRef = info.get(Entity.class, name); if (entityRef == null) return null;
			DataRef<EntityType> entityElementRef = info.get(EntityType.class, name); if (entityElementRef == null) return null;
			DynamicInteger slot = DynamicInteger.getNew(matcher.group(2), info); if (slot == null) return null;
			Collection<ModDamageItemStack> items = ItemAliaser.match(matcher.group(3), info); if (items == null || items.isEmpty()) return null;
			
			return new PlayerItemInSlotMatches(matcher.group(), entityRef, entityElementRef, slot, items);
		}
	}
}
