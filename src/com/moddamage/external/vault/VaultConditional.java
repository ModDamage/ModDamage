package com.ModDamage.External.Vault;

import java.util.regex.Matcher;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Conditionals.Conditional;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;

public abstract class VaultConditional<S> extends Conditional<S>
{
	public final IDataProvider<Double> amountDP;

	protected VaultConditional(Class<S> wantStart, IDataProvider<S> startDP, IDataProvider<Double> amountDP)
	{
		super(wantStart, startDP);
		this.amountDP = amountDP;
	}
	
	@Override
	public final Boolean get(S start, EventData data) throws BailException
	{
		Double amount = amountDP.get(data);
		if (amount == null) return null;
		
		return get(start, amount, data);
	}
	
	protected abstract Boolean get(S start, double amount, EventData data) throws BailException;
	
	
	public static abstract class VaultConditionalParser<S> implements IDataParser<Boolean, S>
	{
		@Override
		public final IDataProvider<Boolean> parse(EventInfo info, IDataProvider<S> startDP, Matcher m, StringMatcher sm)
		{
			IDataProvider<Double> amountDP = DataProvider.parse(info, Double.class, sm.spawn());
			if (amountDP == null) return null;
			
			return sm.acceptIf(parse(info, startDP, amountDP, m, sm));
		}

		protected abstract IDataProvider<Boolean> parse(EventInfo info, IDataProvider<S> startDP, IDataProvider<Double> amountDP, Matcher m,
				StringMatcher sm);
	}
}
