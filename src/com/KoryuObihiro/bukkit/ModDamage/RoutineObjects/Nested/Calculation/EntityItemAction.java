package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Calculation;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageItemStack;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.CalculationRoutine;

public class EntityItemAction extends EntityCalculationRoutine
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
					world.dropItemNaturally(entity.getLocation(), item.toItemStack());
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
	
	protected final ItemAction action;
	protected final Collection<ModDamageItemStack> items;
	public EntityItemAction(String configString, EntityReference entityReference, DynamicInteger integer, ItemAction action, Collection<ModDamageItemStack> items)
	{
		super(configString, entityReference, integer);
		this.action = action;
		this.items = items;
	}
	
	@Override
	protected void doCalculation(TargetEventInfo eventInfo, int input){
		if(!action.requiresPlayer || entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
		{
			for(ModDamageItemStack item : items)
				item.updateAmount(eventInfo);
			action.doAction(entityReference.getEntity(eventInfo), items, value.getValue(eventInfo));
		}
	}

	public static void register()
	{
		CalculationRoutine.registerRoutine(Pattern.compile("(.*)effect\\.(give|drop|take)Item\\.(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends CalculationRoutine.CalculationBuilder
	{
		@Override
		public EntityItemAction getNew(Matcher matcher, DynamicInteger integer)
		{
			Collection<ModDamageItemStack> items = AliasManager.matchItemAlias(matcher.group(3));
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null && !items.isEmpty())
			{
				//ModDamage.addToLogRecord(OutputPreset.INFO, "Item (" + matcher.group(2).toLowerCase() + "): " + matcher.group(1) + ", " + matcher.group(3));
				return new EntityItemAction(matcher.group(), reference, integer, ItemAction.valueOf(matcher.group(2).toUpperCase()), items);
			}
			return null;
		}
	}
}