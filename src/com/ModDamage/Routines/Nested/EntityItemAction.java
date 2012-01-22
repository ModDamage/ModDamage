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
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.ItemAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
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
		private ItemAction(boolean requiresPlayer){ this.requiresPlayer = requiresPlayer;}

		abstract protected void doAction(Entity entity, Collection<ModDamageItemStack> items, int quantity);
	}
	
	protected final ItemAction action;
	protected final Collection<ModDamageItemStack> items;
	protected final EntityReference entityReference;
	protected final DynamicInteger quantity;
	public EntityItemAction(String configString, EntityReference entityReference, ItemAction action, Collection<ModDamageItemStack> items, DynamicInteger quantity)
	{
		super(configString);
		this.entityReference = entityReference;
		this.action = action;
		this.items = items;
		this.quantity = quantity;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo){
		if(!action.requiresPlayer || entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
		{
			for(ModDamageItemStack item : items)
				item.updateAmount(eventInfo);
			
			action.doAction(entityReference.getEntity(eventInfo), items, quantity.getValue(eventInfo));
		}
	}
	
	private static final Pattern enchantPattern = Pattern.compile("enchant\\.(\\w+)\\.(.+)", Pattern.CASE_INSENSITIVE);

	public static void register()
	{
		final NestedRoutineBuilder nrb = new NestedRoutineBuilder();
		Routine.registerRoutine(pattern, new Routine.RoutineBuilder()
			{
				@Override public Routine getNew(Matcher matcher)
				{
					return nrb.getNew(matcher, null);
				}
			});
		NestedRoutine.registerRoutine(pattern, nrb);
	}
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@SuppressWarnings("unchecked")
		@Override
		public EntityItemAction getNew(Matcher matcher, Object nestedContent)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			Collection<ModDamageItemStack> items = ItemAliaser.match(matcher.group(3));
			if(reference != null && !items.isEmpty())
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
						DynamicInteger level = DynamicInteger.getNew(enchantMatcher.group(2));
						
						for (ModDamageItemStack item : items)
						{
							item.addEnchantment(enchantment, level);
						}
					}
				}
				
				DynamicInteger quantity;
				if (matcher.group(4) != null)
					quantity = DynamicInteger.getNew(matcher.group(4));
				else
					quantity = DynamicInteger.getNew("1");
				
				return new EntityItemAction(matcher.group(), reference, ItemAction.valueOf(matcher.group(2).toUpperCase()), items, quantity);
			}
			return null;
		}
	}
}