package com.ModDamage.Expressions;

import java.util.ArrayList;
import java.util.List;

import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.Function.IndexOfFunction;
import com.ModDamage.Expressions.Function.LoreFunction;
import com.ModDamage.Expressions.Function.ReplaceFunction;
import com.ModDamage.Expressions.Function.SubstringFunction;
import com.ModDamage.Expressions.Function.ToIntFunction;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Variables.String.EntityAsString;
import com.ModDamage.Variables.String.EntityString;
import com.ModDamage.Variables.String.PlayerString;
import com.ModDamage.Variables.String.WorldString;

public abstract class StringExp<From> extends DataProvider<String, From>
{
	protected StringExp(Class<From> wantStart, IDataProvider<From> startDP)
	{
		super(wantStart, startDP);
	}

	@Override
	public Class<String> provides() { return String.class; }

	public static void register()
	{
		LiteralString.register();
		EntityString.register();
		EntityAsString.register();
		PlayerString.register();
		WorldString.register();
		
		SubstringFunction.register();
		IndexOfFunction.register();
		ToIntFunction.register();
		LoreFunction.register();
		ReplaceFunction.register();
	}


	@SuppressWarnings("unchecked")
	public static List<IDataProvider<String>> getStrings(Object nestedContent, EventInfo info)
	{
		List<String> strings = new ArrayList<String>();
		if (nestedContent instanceof String)
			strings.add((String)nestedContent);
		else if(nestedContent instanceof List)
			strings.addAll((List<String>) nestedContent);
		else
			return null;

		List<IDataProvider<String>> istrings = new ArrayList<IDataProvider<String>>();
		for(String string : strings)
		{
			istrings.add(new InterpolatedString(string, info, true));
		}

		return istrings;
	}
}