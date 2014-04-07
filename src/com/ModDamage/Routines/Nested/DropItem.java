package com.ModDamage.Routines.Nested;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;

import com.ModDamage.LogUtil;
import com.ModDamage.Alias.ItemAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.LiteralNumber;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routine;

public class DropItem extends NestedRoutine
{
	public static final Pattern pattern = Pattern.compile("(.*?)(?:effect)?\\.dropItem\\.(.+?)(?:\\.\\s*(.+))?", Pattern.CASE_INSENSITIVE);

	protected final Collection<ModDamageItemStack> items;
	protected final IDataProvider<Location> locationDP;
	protected final IDataProvider<? extends Number> quantity;
	public DropItem(ScriptLine scriptLine, IDataProvider<Location> locationDP, Collection<ModDamageItemStack> items, IDataProvider<? extends Number> quantity)
	{
		super(scriptLine);
		this.locationDP = locationDP;
		this.items = items;
		this.quantity = quantity;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Location loc = locationDP.get(data);
		if (loc == null) return;
		
        for(ModDamageItemStack item : items)
            item.update(data);

        
        Number quant = this.quantity.get(data);
        if (quant == null) return;
        
        int quantity = quant.intValue();

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

	private static final NestedRoutineFactory nrb = new NestedRoutineFactory();
	public static void registerRoutine()
	{
		Routine.registerRoutine(pattern, new Routine.RoutineFactory()
			{
				@Override public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
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

	protected static class NestedRoutineFactory extends NestedRoutine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			IDataProvider<Location> locationDP = DataProvider.parse(scriptLine, info, Location.class, name);
			if (locationDP == null) return null;
			Collection<ModDamageItemStack> items = ItemAliaser.match(scriptLine, matcher.group(2), info);
			if(items == null || items.isEmpty()) return null;
			
			IDataProvider<? extends Number> quantity;
			if (matcher.group(3) != null)
				quantity = DataProvider.parse(scriptLine, info, Integer.class, matcher.group(3));
			else
				quantity = new LiteralNumber(1);

            if (quantity == null) return null;

            LogUtil.info("Drop item at " + locationDP + ": " + items);
			
			DropItem routine = new DropItem(scriptLine, locationDP, items, quantity);
			return new NestedRoutineBuilder(routine, routine.routines, info.chain(myInfo));
		}
	}
}