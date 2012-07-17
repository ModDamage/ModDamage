package com.ModDamage.Conditionals;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class Chance implements IDataProvider<Boolean>
{
	public static final Pattern pattern = Pattern.compile("chance\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	protected final Random random = new Random();
	protected final IDataProvider<Integer> probability;
	
	public Chance(IDataProvider<Integer> probability)
	{
		this.probability = probability;
	}

	@Override
	public Boolean get(EventData data) throws BailException
	{
		return Math.abs(random.nextInt()%101) <= probability.get(data);
	}
	
	@Override
	public Class<Boolean> provides() { return Boolean.class; }
	
	public static void register()
	{
		DataProvider.register(Boolean.class, null, pattern, new IDataParser<Boolean, Object>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Object> start, Matcher m, StringMatcher sm)
				{
					IDataProvider<Integer> probability = DataProvider.parse(info, Integer.class, sm.spawn());
					
					return new Chance(probability);
				}
			});
	}
}
