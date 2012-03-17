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
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class PlayerHasItem extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.has((?:all)?items|item)\\.([\\w,@*]+)", Pattern.CASE_INSENSITIVE);
	
	private final DataRef<Entity> entityRef;
	private final DataRef<EntityType> entityTypeRef;
	private final boolean allItems;
	private final Collection<ModDamageItemStack> items;
	
	public PlayerHasItem(String configString, DataRef<Entity> entityRef, DataRef<EntityType> entityElementRef, boolean allItems, Collection<ModDamageItemStack> items)
	{
		super(configString);
		this.entityRef = entityRef;
		this.entityTypeRef = entityElementRef;
		this.allItems = allItems;
		this.items = items;
	}

	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		if(entityTypeRef.get(data).matches(EntityType.PLAYER))
		{
			for(ModDamageItemStack item : items)
				item.update(data);
			Inventory inventory = ((Player)entityRef.get(data)).getInventory();
			if(allItems)
			{
				for(ModDamageItemStack item : items)
				{
					ItemStack temp = item.toItemStack();
					if(!inventory.contains(temp.getType(), temp.getAmount()))
						return false;
				}
				return true;
			}
			else
			{
				for(ModDamageItemStack item : items)
				{
					ItemStack temp = item.toItemStack();
					if(inventory.contains(temp.getType(), temp.getAmount()))
						return true;
				}
				return false;
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
		public PlayerHasItem getNew(Matcher matcher, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			DataRef<Entity> entityRef = info.get(Entity.class, name); if (entityRef == null) return null;
			DataRef<EntityType> entityElementRef = info.get(EntityType.class, name); if (entityElementRef == null) return null;
			Collection<ModDamageItemStack> items = ItemAliaser.match(matcher.group(3), info);
			if(items != null && !items.isEmpty())
				return new PlayerHasItem(matcher.group(), entityRef, entityElementRef, matcher.group(2).equalsIgnoreCase("allitems"), items);
			return null;
		}
	}
}
