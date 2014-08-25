package com.moddamage.conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.LogUtil;
import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.alias.ItemAliaser;
import com.moddamage.backend.BailException;
import com.moddamage.backend.ItemHolder;
import com.moddamage.backend.ModDamageItemStack;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

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
						LogUtil.warning_strong("Using the material version is deprecated. Please use '"+ itemDP +".matches."+ m.group(2) +"' instead.");
					
					return new ItemMatches(itemDP, matchedItems);
				}
			});
	}
}
