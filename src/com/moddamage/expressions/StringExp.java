package com.moddamage.expressions;

import com.moddamage.eventinfo.EventInfo;
import com.moddamage.expressions.function.*;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.variables.string.EntityAsString;
import com.moddamage.variables.string.EntityString;
import com.moddamage.variables.string.PlayerString;
import com.moddamage.variables.string.WorldString;

import java.util.ArrayList;
import java.util.List;

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
		RegexReplaceFunction.register();
		FormatFunction.register();
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