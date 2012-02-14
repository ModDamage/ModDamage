package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.Backend.Aliasing.ItemAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class PlayerHasItem extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.has((?:all)?items|item)\\.([\\w*]+)", Pattern.CASE_INSENSITIVE);
	final DataRef<Entity> entityRef;
	final DataRef<ModDamageElement> entityElementRef;
	final boolean strict;
	final Collection<ModDamageItemStack> items;
	public PlayerHasItem(DataRef<Entity> entityRef, DataRef<ModDamageElement> entityElementRef, boolean strict, Collection<ModDamageItemStack> items)
	{
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.strict = strict;
		this.items = items;
	}

	@Override
	public boolean evaluate(EventData data)
	{
		if(entityElementRef.get(data).matchesType(ModDamageElement.PLAYER))
		{
			for(ModDamageItemStack item : items)
				item.updateAmount(data);
			if(strict)
			{
				for(ModDamageItemStack item : items)
				{
					ItemStack temp = item.toItemStack();
					if(!((Player)entityRef.get(data)).getInventory().contains(temp.getType(), temp.getAmount()))
						return false;
				}
				return true;
			}
			else
			{
				for(ModDamageItemStack item : items)
				{
					ItemStack temp = item.toItemStack();
					if(((Player)entityRef.get(data)).getInventory().contains(temp.getType(), temp.getAmount()))
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
			DataRef<Entity> entityRef = info.get(Entity.class, name);
			DataRef<ModDamageElement> entityElementRef = info.get(ModDamageElement.class, name);
			Collection<ModDamageItemStack> items = ItemAliaser.match(matcher.group(3), info);
			if(entityRef != null && !items.isEmpty())
				return new PlayerHasItem(entityRef, entityElementRef, matcher.group(2).equalsIgnoreCase("allitems"), items);
			return null;
		}
	}
}
