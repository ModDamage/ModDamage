package com.ModDamage.Routines.Nested;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.Alias.ItemAliaser;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EnchantmentsRef;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Routines.Routine;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Variables.Ints.Constant;

public class EntityItemAction extends NestedRoutine
{
	public static final Pattern pattern = Pattern.compile("(.*)effect\\.(give|drop|take)Item\\.(.+?)(?:\\.\\s*(.+))?", Pattern.CASE_INSENSITIVE);
	
	protected enum ItemAction
	{
		DROP(false)
		{
			@Override
			protected void doAction(Entity entity, ItemStack item)
			{
				entity.getWorld().dropItemNaturally(entity.getLocation(), item);
			}
		},
		GIVE(true)
		{
			@Override
			protected void doAction(Entity entity, ItemStack item)
			{
				((Player)entity).getInventory().addItem(item);
			}
		},
		TAKE(true)
		{
			@Override
			protected void doAction(Entity entity, ItemStack item)
			{
				((Player)entity).getInventory().removeItem(item);
			}
		};
		
		protected final boolean requiresPlayer;
		private ItemAction(boolean requiresPlayer){ this.requiresPlayer = requiresPlayer; }

		abstract protected void doAction(Entity entity, ItemStack item);
	}
	
	protected final ItemAction action;
	protected final Collection<ModDamageItemStack> items;
	protected final DataRef<LivingEntity> entityRef;
	protected final Routines routines;
	protected final IntegerExp quantity;
	public EntityItemAction(String configString, DataRef<LivingEntity> entityRef, ItemAction action, Collection<ModDamageItemStack> items, IntegerExp quantity, Routines routines)
	{
		super(configString);
		this.entityRef = entityRef;
		this.action = action;
		this.items = items;
		this.quantity = quantity;
		this.routines = routines;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
		LivingEntity entity = entityRef.get(data);
		
		if(!action.requiresPlayer || entity instanceof Player)
		{
			for(ModDamageItemStack item : items)
				item.update(data);
			
			int quantity = this.quantity.getValue(data);
			
			for (int i = 0; i < quantity; i++)
			{
				for (ModDamageItemStack item : items)
				{
					ItemStack vanillaItem = item.toItemStack();
					
					if (routines != null)
					{
						// have to copy the enchantments map because it is immutable
						Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>(vanillaItem.getEnchantments());
						EnchantmentsRef enchants = new EnchantmentsRef(enchantments);
						routines.run(myInfo.makeChainedData(data, vanillaItem, enchants));
						for (Entry<Enchantment, Integer> entry : enchantments.entrySet())
						{
							if (entry.getValue() == 0)
								vanillaItem.removeEnchantment(entry.getKey());
							else
								vanillaItem.addEnchantment(entry.getKey(), entry.getValue());
						}
					}
					
					action.doAction(entity, vanillaItem);
				}
			}
		}
	}
	
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
	
	private static final EventInfo myInfo = new SimpleEventInfo(
			ItemStack.class, 		"item",
			EnchantmentsRef.class,	"-enchantments");
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public EntityItemAction getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			DataRef<LivingEntity> entityRef = info.get(LivingEntity.class, name); if (entityRef == null) return null;
			Collection<ModDamageItemStack> items = ItemAliaser.match(matcher.group(3), info);
			if(items != null && !items.isEmpty())
			{
				Routines routines = null;
				if (nestedContent != null)
					routines = RoutineAliaser.parseRoutines(nestedContent, info.chain(myInfo));
				
				
				IntegerExp quantity;
				if (matcher.group(4) != null)
					quantity = IntegerExp.getNew(matcher.group(4), info);
				else
					quantity = new Constant(1);
				
				return new EntityItemAction(matcher.group(), entityRef, ItemAction.valueOf(matcher.group(2).toUpperCase()), items, quantity, routines);
			}
			return null;
		}
	}
}