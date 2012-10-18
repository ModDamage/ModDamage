package com.ModDamage.Routines.Nested;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.Alias.ItemAliaser;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EnchantmentsRef;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routine;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Variables.Int.Constant;

import static com.ModDamage.PluginConfiguration.OutputPreset;

public class EntityItemAction extends NestedRoutine
{
	public static final Pattern pattern = Pattern.compile("(.*?)(?:effect)?\\.(give|take)Item\\.(.+?)(?:\\.\\s*(.+))?", Pattern.CASE_INSENSITIVE);
	
	protected enum ItemAction
	{
		GIVE
		{
			@Override
			protected void doAction(HumanEntity entity, ItemStack item)
			{
				entity.getInventory().addItem(item);
			}
		},
		TAKE
		{
			@Override
			protected void doAction(HumanEntity entity, ItemStack item)
			{
				entity.getInventory().removeItem(item);
			}
		};

		abstract protected void doAction(HumanEntity player, ItemStack item);
	}
	
	protected final ItemAction action;
	protected final Collection<ModDamageItemStack> items;
	protected final IDataProvider<HumanEntity> humanDP;
	protected final Routines routines;
	protected final IDataProvider<Integer> quantity;

	public EntityItemAction(String configString, IDataProvider<HumanEntity> humanDP, ItemAction action, Collection<ModDamageItemStack> items, IDataProvider<Integer> quantity, Routines routines)
	{
		super(configString);
		this.humanDP = humanDP;
		this.action = action;
		this.items = items;
		this.quantity = quantity;
		this.routines = routines;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
		HumanEntity entity = humanDP.get(data);

        for(ModDamageItemStack item : items)
            item.update(data);

        int quantity = this.quantity.get(data);

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
			EnchantmentsRef.class,	"enchantments");
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public EntityItemAction getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
            String action = matcher.group(2).toUpperCase();

			IDataProvider<HumanEntity> humanDP = DataProvider.parse(info, HumanEntity.class, name); if (humanDP == null) return null;
			Collection<ModDamageItemStack> items = ItemAliaser.match(matcher.group(3), info);
			if(items != null && !items.isEmpty())
			{
				Routines routines = null;
				if (nestedContent != null)
					routines = RoutineAliaser.parseRoutines(nestedContent, info.chain(myInfo));

				
				IDataProvider<Integer> quantity;
				if (matcher.group(4) != null)
					quantity = DataProvider.parse(info, Integer.class, matcher.group(4));
				else
					quantity = new Constant(1);
                
                if (quantity == null) return null;


                ModDamage.addToLogRecord(OutputPreset.INFO, action.charAt(0) + action.substring(1).toLowerCase() + " at/to " + humanDP + ": " + items);
				
				return new EntityItemAction(matcher.group(), humanDP, ItemAction.valueOf(action), items, quantity, routines);
			}
			return null;
		}
	}
}