package com.ModDamage.Conditionals;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.BaseDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class Chance implements IDataProvider<Boolean>
{
	public static final Pattern pattern = Pattern.compile("chance\\.", Pattern.CASE_INSENSITIVE);
	
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
		DataProvider.register(Boolean.class, pattern, new BaseDataParser<Boolean>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, Matcher m, StringMatcher sm)
				{
					IDataProvider<Integer> probability = DataProvider.parse(info, Integer.class, sm.spawn());
					if (probability == null) return null;
					
					sm.accept();
					return new Chance(probability);
				}
			});
	}
	
	@Override
	public String toString()
	{
		return "chance." + probability;
	}
}
