package com.ModDamage.Parsing;

import java.util.regex.Matcher;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventInfo;

public interface IDataParser<T, S>
{
	IDataProvider<T> parse(ScriptLine line, EventInfo info, IDataProvider<S> startDP, Matcher m, StringMatcher sm);
}