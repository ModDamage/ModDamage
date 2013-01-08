package com.ModDamage.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Alias.ItemAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;

public class ItemMatches extends Conditional<ItemHolder> 
{
	public static final Pattern pattern = Pattern.compile("\\.(material|matches)\\.([\\w,]+)", Pattern.CASE_INSENSITIVE);
	
	private final Collection<ModDamageItemStack> items;
	
	public ItemMatches(IDataProvider<ItemHolder> itemDP, Collection<ModDamageItemStack> items)
	{
		super(ItemHolder.class, itemDP);
		this.items = items;
	}
	@Override
	public Boolean get(ItemHolder item, EventData data) throws BailException
	{
		for (ModDamageItemStack mdis : items)
		{
			mdis.update(data);
			if (mdis.matches(item.getItem()))
				return true;
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return startDP + ".matches." + Utils.joinBy(",", items);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, ItemHolder.class, pattern, new IDataParser<Boolean, ItemHolder>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<ItemHolder> itemDP, Matcher m, StringMatcher sm)
				{
					Collection<ModDamageItemStack> matchedItems = ItemAliaser.match(m.group(2), info);
					if(matchedItems == null || matchedItems.isEmpty()) return null;
						
					if (m.group(1).equalsIgnoreCase("material"))
						ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, "Using the material version is deprecated. Please use '"+ itemDP +".matches."+ m.group(2) +"' instead.");
					
					return new ItemMatches(itemDP, matchedItems);
				}
			});
	}
}
