package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.Backend.Aliasing.ItemAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class EntityWielding extends Conditional 
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.(?:is)?wielding\\.([\\w,@*]+)", Pattern.CASE_INSENSITIVE);
	private final DataRef<Entity> entityRef;
	private final Collection<ModDamageItemStack> items;
	public EntityWielding(DataRef<Entity> entityRef, Collection<ModDamageItemStack> items)
	{  
		this.entityRef = entityRef;
		this.items = items;
	}
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		Entity entity = entityRef.get(data);
		if (entity instanceof Player)
		{
			ItemStack wieldedItem = ((Player)entity).getItemInHand();
			for (ModDamageItemStack item : items)
			{
				item.update(data);
				if (item.matches(wieldedItem))
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
		public EntityWielding getNew(Matcher matcher, EventInfo info)
		{
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());
			Collection<ModDamageItemStack> matchedItems = ItemAliaser.match(matcher.group(2), info);
			if(entityRef != null && !matchedItems.isEmpty())
				return new EntityWielding(entityRef, matchedItems);
			return null;
		}
	}
}
