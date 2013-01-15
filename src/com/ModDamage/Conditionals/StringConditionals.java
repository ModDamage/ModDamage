package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;

public class StringConditionals extends Conditional<String>
{
	public static final Pattern pattern = Pattern.compile("\\.(i)?("+Utils.joinBy("|", StringConditional.values())+")\\.", Pattern.CASE_INSENSITIVE);
	
	
	private enum StringConditional
	{
		CONTAINS
		{
			@Override
			public boolean isTrue(String str, String otherStr)
			{
				return str.contains(otherStr);
			}
		},
		IN
		{
			@Override
			public boolean isTrue(String str, String otherStr)
			{
				return otherStr.contains(str);
			}
		},
		STARTSWITH
		{
			@Override
			public boolean isTrue(String str, String otherStr)
			{
				return str.startsWith(otherStr);
			}
		},
		ENDSWITH
		{
			@Override
			public boolean isTrue(String str, String otherStr)
			{
				return str.endsWith(otherStr);
			}
		};
		
		abstract public boolean isTrue(String str, String otherStr);
	}
	
	private final boolean caseInsensitive;
	private final StringConditional conditional;
	private final IDataProvider<String> otherString;
	
	protected StringConditionals(IDataProvider<String> stringDP, boolean caseInsensitive, StringConditional statusType, IDataProvider<String> otherString)
	{
		super(String.class, stringDP);
		this.caseInsensitive = caseInsensitive;
		this.conditional = statusType;
		this.otherString = otherString;
	}
	@Override
	public Boolean get(String str, EventData data) throws BailException
	{
		String otherStr = otherString.get(data);
		if (caseInsensitive) {
			str = str.toLowerCase();
			otherStr = otherStr.toLowerCase();
		}
		return conditional.isTrue(str, otherStr);
	}
	
	@Override
	public String toString()
	{
		return startDP + "." + (caseInsensitive?"i":"") + conditional.name().toLowerCase() + "." + otherString;
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, String.class, pattern, new IDataParser<Boolean, String>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<String> entityDP, Matcher m, StringMatcher sm)
				{
					StringConditional statusType = null;
					for(StringConditional type : StringConditional.values())
						if(m.group(2).equalsIgnoreCase(type.name()))
								statusType = type;
					if(statusType == null) return null;
					
					boolean caseInsensitive = (m.group(1) != null);
					
					IDataProvider<String> otherString = DataProvider.parse(info, String.class, sm);
					
					return new StringConditionals(entityDP, caseInsensitive, statusType, otherString);
				}
			});
	}
}
