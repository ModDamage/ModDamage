package com.moddamage.external.vault;

import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.conditionals.Conditional;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

import java.util.regex.Matcher;

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
