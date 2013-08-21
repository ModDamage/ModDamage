package com.ModDamage.Parsing;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.EventInfo;

@SuppressWarnings("rawtypes")
public abstract class FunctionParser<T, S> implements IDataParser<T, S> {
    public static final Pattern startParenPattern = Pattern.compile("\\(\\s*");
    public static final Pattern commaPattern = Pattern.compile("\\s*,\\s*");
    public static final Pattern endParenPattern = Pattern.compile("\\s*\\)");

	public final Class[] parameters;

	public FunctionParser(Class... parameters) {
        this.parameters = parameters;
    }

    @SuppressWarnings("unchecked")
	@Override
    public IDataProvider<T> parse(EventInfo info, IDataProvider<S> startDP, Matcher m, StringMatcher sm) {
        IDataProvider[] args = new IDataProvider[parameters.length];

        if (!sm.matchesFront(startParenPattern)) return null;
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0 && !sm.matchesFront(commaPattern)) return null;

            args[i] = DataProvider.parse(info, parameters[i], sm.spawn(), false, true, (i == parameters.length-1)? endParenPattern : commaPattern);
            if (args[i] == null) return null;
        }
        if (!sm.matchesFront(endParenPattern)) return null;

        return sm.acceptIf(makeProvider(info, startDP, args));
    }

	protected abstract IDataProvider<T> makeProvider(EventInfo info, IDataProvider<S> startDP, IDataProvider[] arguments);
}
