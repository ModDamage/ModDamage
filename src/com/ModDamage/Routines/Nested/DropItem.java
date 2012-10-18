package com.ModDamage.Routines.Nested;

import com.ModDamage.Alias.ItemAliaser;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EnchantmentsRef;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.EventInfo.*;
import com.ModDamage.ModDamage;
import com.ModDamage.Routines.Routine;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Variables.Int.Constant;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ModDamage.PluginConfiguration.OutputPreset;

public class DropItem extends NestedRoutine
{
	public static final Pattern pattern = Pattern.compile("(.*?)(?:effect)?\\.dropItem\\.(.+?)(?:\\.\\s*(.+))?", Pattern.CASE_INSENSITIVE);

	protected final Collection<ModDamageItemStack> items;
	protected final IDataProvider<Location> locationDP;
	protected final Routines routines;
	protected final IDataProvider<Integer> quantity;
	public DropItem(String configString, IDataProvider<Location> locationDP, Collection<ModDamageItemStack> items, IDataProvider<Integer> quantity, Routines routines)
	{
		super(configString);
		this.locationDP = locationDP;
		this.items = items;
		this.quantity = quantity;
		this.routines = routines;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Location loc = locationDP.get(data);

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

                loc.getWorld().dropItemNaturally(loc, vanillaItem);
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

	protected static class NestedRoutineBuilder extends RoutineBuilder
	{
		@Override
		public DropItem getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			IDataProvider<Location> locationDP = DataProvider.parse(info, Location.class, name); if (locationDP == null) return null;
			Collection<ModDamageItemStack> items = ItemAliaser.match(matcher.group(2), info);
			if(items != null && !items.isEmpty())
			{
				Routines routines = null;
				if (nestedContent != null)
					routines = RoutineAliaser.parseRoutines(nestedContent, info.chain(myInfo));

				
				IDataProvider<Integer> quantity;
				if (matcher.group(3) != null)
					quantity = DataProvider.parse(info, Integer.class, matcher.group(3));
				else
					quantity = new Constant(1);

                if (quantity == null) return null;

                ModDamage.addToLogRecord(OutputPreset.INFO, "Drop item at " + locationDP + ": " + items);
				
				return new DropItem(matcher.group(), locationDP, items, quantity, routines);
			}
			return null;
		}
	}
}