package com.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.AliasManager;
import com.ModDamage.RoutineObjects.Nested.Conditional;

public class PlayerHasItem extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.has((?:all)?items|item)\\.([\\w*]+)", Pattern.CASE_INSENSITIVE);
	final EntityReference entityReference;
	private final boolean strict;
	private final Collection<ModDamageItemStack> items;
	public PlayerHasItem(EntityReference entityReference, boolean strict, Collection<ModDamageItemStack> items)
	{
		this.entityReference = entityReference;
		this.strict = strict;
		this.items = items;
	}

	@Override
	public boolean evaluate(TargetEventInfo eventInfo)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
		{
			for(ModDamageItemStack item : items)
				item.updateAmount(eventInfo);
			if(strict)
			{
				for(ModDamageItemStack item : items)
				{
					ItemStack temp = item.toItemStack();
					if(!((Player)entityReference.getEntity(eventInfo)).getInventory().contains(temp.getType(), temp.getAmount()))
						return false;
				}
				return true;
			}
			else
			{
				for(ModDamageItemStack item : items)
				{
					ItemStack temp = item.toItemStack();
					if(((Player)entityReference.getEntity(eventInfo)).getInventory().contains(temp.getType(), temp.getAmount()))
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
		public PlayerHasItem getNew(Matcher matcher)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			Collection<ModDamageItemStack> items = AliasManager.matchItemAlias(matcher.group(3));
			if(reference != null && !items.isEmpty())
				return new PlayerHasItem(reference, matcher.group(2).equalsIgnoreCase("allitems"), items);
			return null;
		}
	}
}
