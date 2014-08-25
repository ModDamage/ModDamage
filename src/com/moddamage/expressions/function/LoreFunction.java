package com.moddamage.expressions.function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.LogUtil;
import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.backend.ItemHolder;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.parsing.SettableDataProvider;

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
