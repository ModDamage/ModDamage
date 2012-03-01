package com.ModDamage.Routines.Nested;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EntityType;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.Backend.Aliasing.ItemAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Routines.Routine;

public class EntityItemAction extends NestedRoutine
{
	public static final Pattern pattern = Pattern.compile("(.*)effect\\.(give|drop|take)Item\\.(.+?)(?:\\.\\s*(.+))?", Pattern.CASE_INSENSITIVE);
	
	protected enum ItemAction
	{
		DROP(false)
		{
			@Override
			protected void doAction(Entity entity, Collection<ModDamageItemStack> items, int quantity)
			{
				World world = entity.getWorld();
				for (int i = 0; i < quantity; i++)
					for(ModDamageItemStack item : items)
						world.dropItemNaturally(entity.getLocation(), item.toItemStack());
			}
		},
		GIVE(true)
		{
			@Override
			protected void doAction(Entity entity, Collection<ModDamageItemStack> items, int quantity)
			{
				ItemStack[] vanillaItems = ModDamageItemStack.toItemStacks(items);
				for (int i = 0; i < quantity; i++)
					((Player)entity).getInventory().addItem(vanillaItems);
			}
		},
		TAKE(true)
		{
			@Override
			protected void doAction(Entity entity, Collection<ModDamageItemStack> items, int quantity)
			{
				ItemStack[] vanillaItems = ModDamageItemStack.toItemStacks(items);
				for (int i = 0; i < quantity; i++)
					((Player)entity).getInventory().removeItem(vanillaItems);
			}
		};
		
		protected final boolean requiresPlayer;
		private ItemAction(boolean requiresPlayer){ this.requiresPlayer = requiresPlayer; }

		abstract protected void doAction(Entity entity, Collection<ModDamageItemStack> items, int quantity);
	}
	
	protected final ItemAction action;
	protected final Collection<ModDamageItemStack> items;
	protected final DataRef<Entity> entityRef;
	protected final DataRef<EntityType> entityElementRef;
	protected final DynamicInteger quantity;
	public EntityItemAction(String configString, DataRef<Entity> entityRef, DataRef<EntityType> entityElementRef, ItemAction action, Collection<ModDamageItemStack> items, DynamicInteger quantity)
	{
		super(configString);
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.action = action;
		this.items = items;
		this.quantity = quantity;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
		if(!action.requiresPlayer || entityElementRef.get(data).matches(EntityType.PLAYER))
		{
			for(ModDamageItemStack item : items)
				item.update(data);
			
			action.doAction(entityRef.get(data), items, quantity.getValue(data));
		}
	}
	
	private static final Pattern enchantPattern = Pattern.compile("enchant\\.(\\w+)\\.(.+)", Pattern.CASE_INSENSITIVE);

	
	private static final NestedRoutineBuilder nrb = new NestedRoutineBuilder();
	public static void registerRoutine()
	{
		Routine.registerRoutine(pattern, new Routine.RoutineBuilder()
			{
				@Override public Routine getNew(Matcher matcher, EventInfo info)
				{
					return nrb.getNew(matcher, null, info);
				}
			});
	}
	
	public static void registerNested()
	{
		NestedRoutine.registerRoutine(pattern, nrb);
	}
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@SuppressWarnings("unchecked")
		@Override
		public EntityItemAction getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			DataRef<Entity> entityRef = info.get(Entity.class, name); if (entityRef == null) return null;
			DataRef<EntityType> entityElementRef = info.get(EntityType.class, name); if (entityElementRef == null) return null;
			Collection<ModDamageItemStack> items = ItemAliaser.match(matcher.group(3), info);
			if(items != null && !items.isEmpty())
			{
				if (nestedContent != null)
				{
					if (nestedContent instanceof String) nestedContent = Arrays.asList(nestedContent);
					
					for (String string : (List<String>)nestedContent)
					{
						Matcher enchantMatcher = enchantPattern.matcher(string);
						if (!enchantMatcher.matches())
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "This routine is invalid inside of an item action routine: \"" + string + "\"");
							continue;
						}
						
						Enchantment enchantment = Enchantment.getByName(enchantMatcher.group(1).toUpperCase());
						if (enchantment == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid enchantment: " + enchantMatcher.group(1));
							return null;
						}
						DynamicInteger level = DynamicInteger.getNew(enchantMatcher.group(2), info);
						
						for (ModDamageItemStack item : items)
						{
							item.addEnchantment(enchantment, level);
						}
					}
				}
				
				DynamicInteger quantity;
				if (matcher.group(4) != null)
					quantity = DynamicInteger.getNew(matcher.group(4), info);
				else
					quantity = DynamicInteger.getNew("1", info);
				
				return new EntityItemAction(matcher.group(), entityRef, entityElementRef, ItemAction.valueOf(matcher.group(2).toUpperCase()), items, quantity);
			}
			return null;
		}
	}
}