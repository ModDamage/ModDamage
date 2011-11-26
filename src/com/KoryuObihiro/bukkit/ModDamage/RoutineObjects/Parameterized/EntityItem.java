package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageItemStack;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.NestedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityItem extends ParameterizedRoutine
{
	protected enum ItemAction
	{
		DROP(false)
		{
			@Override
			protected void doAction(Entity entity, Collection<ModDamageItemStack> items, int quantity)
			{
				World world = entity.getWorld();
				for(int i = 0; i < quantity; i++)
					for(ModDamageItemStack item : items)
					world.dropItem(entity.getLocation(), item.toItemStack());
			}
		},
		GIVE(true)
		{
			@Override
			protected void doAction(Entity entity, Collection<ModDamageItemStack> items, int quantity)
			{
				ItemStack[] vanillaItems = ModDamageItemStack.toItemStacks(items);
				for(int i = 0; i < quantity; i++)
					((Player)entity).getInventory().addItem(vanillaItems);
			}
		},
		TAKE(true)
		{
			@Override
			protected void doAction(Entity entity, Collection<ModDamageItemStack> items, int quantity)
			{
				ItemStack[] vanillaItems = ModDamageItemStack.toItemStacks(items);
				for(int i = 0; i < quantity; i++)
					((Player)entity).getInventory().removeItem(vanillaItems);
			}
		};
		
		protected final boolean requiresPlayer;
		private ItemAction(boolean requiresPlayer){ this.requiresPlayer = requiresPlayer;}

		abstract protected void doAction(Entity entity, Collection<ModDamageItemStack> items, int quantity);
	}
	
	protected final EntityReference entityReference;
	protected final ItemAction action;
	protected final Collection<ModDamageItemStack> items;
	protected final DynamicInteger integer;
	public EntityItem(String configString, EntityReference entityReference, ItemAction action, Collection<ModDamageItemStack> items, DynamicInteger integer)
	{
		super(configString);
		this.entityReference = entityReference;
		this.action = action;
		this.items = items;
		this.integer = integer;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo) 
	{
		if(!action.requiresPlayer || entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
		{
			for(ModDamageItemStack item : items)
				item.updateAmount(eventInfo);
			action.doAction(entityReference.getEntity(eventInfo), items, integer.getValue(eventInfo));
		}
	}

	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(\\w+)effect\\.(give|drop|take)Item(?:\\.([\\w\\*]+))?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public EntityItem getNew(Matcher matcher, Object nestedContent)
		{
			List<Routine> routines = new ArrayList<Routine>();
			Collection<ModDamageItemStack> items = null;
			if(matcher.group(3) != null)
			{
				items = AliasManager.matchItemAlias(matcher.group(3));
				EntityReference reference = EntityReference.match(matcher.group(2));
				if(reference != null && !items.isEmpty())
				{
					ModDamage.addToLogRecord(OutputPreset.INFO, "Item (" + matcher.group(2).toLowerCase() + "): " + matcher.group(1) + ", " + matcher.group(3));
					return new EntityItem(matcher.group(), reference, ItemAction.valueOf(matcher.group(2).toUpperCase()), items, DynamicInteger.getNew(routines));
				}
			}
			else
			{
				EntityReference reference = EntityReference.match(matcher.group(2));
				boolean failFlag = reference != null;
				LinkedHashMap<String, Object> nestedMap = ModDamage.getPluginConfiguration().castToStringMap("Item routine", nestedContent);
				if(nestedMap != null)
				{
					Object itemsObject = PluginConfiguration.getCaseInsensitiveValue(nestedMap, "Items");
					if(itemsObject != null)
					{
						if(itemsObject instanceof String)//TODO Make this parsing stuff part of the Aliaser library - other routines might use something similar when parameterizing for other kinds of aliases.
							items = AliasManager.matchItemAlias((String)itemsObject);
						else if(itemsObject instanceof List)
							for(Object nestedItemObject : (List<?>)itemsObject)
							{
								if(nestedItemObject instanceof String)
								{
									Collection<ModDamageItemStack> stringValue = AliasManager.matchItemAlias((String)nestedItemObject);
									if(!stringValue.isEmpty())
										failFlag = true;
								}
								else failFlag = true;
							}
					}
					else failFlag = false;
					
					DynamicInteger integer = ParameterizedRoutine.getRoutineParameter(nestedMap, "Quantity");
					if(integer == null) failFlag = true;
					
					if(!failFlag)
						return new EntityItem(matcher.group(), reference, ItemAction.valueOf(matcher.group(2).toUpperCase()), items, DynamicInteger.getNew(routines));
				}
			}
			return null;
		}
	}
}