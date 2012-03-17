package com.ModDamage.Routines.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class BooleanData extends Conditional 
{
	public static final Pattern pattern = Pattern.compile("\\w+", Pattern.CASE_INSENSITIVE);
	
	private final DataRef<Boolean> boolRef;
	protected BooleanData(String configString, DataRef<Boolean> boolRef)
	{
		super(configString);
		this.boolRef = boolRef;
	}
	
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		return boolRef.get(data);
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public BooleanData getNew(Matcher matcher, EventInfo info)
		{
			DataRef<Boolean> boolRef = info.get(Boolean.class, matcher.group(), false);
			if (boolRef == null) return null;
			return new BooleanData(matcher.group(), boolRef);
		}
	}
}
