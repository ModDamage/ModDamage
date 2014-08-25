package com.ModDamage.Expressions.Function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.LogUtil;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.SettableDataProvider;

public class LoreFunction extends SettableDataProvider<String, ItemHolder>
{
	private final IDataProvider<Integer> indexDP;

	private LoreFunction(IDataProvider<ItemHolder> stringDP, IDataProvider<Integer> indexDP)
	{
		super(ItemHolder.class, stringDP);
		this.indexDP = indexDP;
	}

	@Override
	public String get(ItemHolder holder, EventData data) throws BailException
	{
		Integer index = indexDP.get(data);
		if (index == null) return null;
		
		return holder.getLore(index);
	}

	@Override
	public void set(ItemHolder holder, EventData data, String value) throws BailException
	{
		Integer index = indexDP.get(data);
		if (index == null) return;
		
		holder.setLore(index, value);
	}

	@Override
	public boolean isSettable()
	{
		return true;
	}

	@Override
	public Class<String> provides() { return String.class; }

	static final Pattern endPattern = Pattern.compile("\\s*\\)");
	public static void register()
	{
		DataProvider.register(String.class, ItemHolder.class, Pattern.compile("_lore\\("), new IDataParser<String, ItemHolder>()
			{
				@Override
				public IDataProvider<String> parse(EventInfo info, IDataProvider<ItemHolder> holderDP, Matcher m, StringMatcher sm)
				{
					IDataProvider<Integer> indexDP = DataProvider.parse(info, Integer.class, sm.spawn());
					if (indexDP == null) return null;

					Matcher endMatcher = sm.matchFront(endPattern);
					if (endMatcher == null)
					{
						LogUtil.error("Missing end paren: \"" + sm.string + "\"");
						return null;
					}

					return sm.acceptIf(new LoreFunction(holderDP, indexDP));
				}
			});
	}

	@Override
	public String toString()
	{
		return startDP + "_lore(" + indexDP + ")";
	}
}
