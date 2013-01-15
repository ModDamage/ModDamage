package com.ModDamage.Routines.Nested;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.ItemAliaser;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.LiteralInteger;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routine;
import com.ModDamage.Routines.Routines;

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
            	ItemHolder holder = new ItemHolder(item.toItemStack());

                if (routines != null)
                {
                    // have to copy the enchantments map because it is immutable
                    routines.run(myInfo.makeChainedData(data, holder));
                }

                loc.getWorld().dropItemNaturally(loc, holder.getItem());
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
			ItemHolder.class, 		"item");

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
					quantity = new LiteralInteger(1);

                if (quantity == null) return null;

                ModDamage.addToLogRecord(OutputPreset.INFO, "Drop item at " + locationDP + ": " + items);
				
				return new DropItem(matcher.group(), locationDP, items, quantity, routines);
			}
			return null;
		}
	}
}