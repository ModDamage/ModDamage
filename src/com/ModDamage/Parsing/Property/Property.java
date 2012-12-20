package com.ModDamage.Parsing.Property;


import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.StringMatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Property<T, S> {
    public final String name;
    public final Class<T> provides;
    public final Class<S> startsWith;

    public final Pattern pattern;


    public Property(String name, Class<T> provides, Class<S> startsWith)
    {
        this(name, provides, startsWith, Pattern.compile("_"+name, Pattern.CASE_INSENSITIVE));
    }

    public Property(String name, Class<T> provides, Class<S> startsWith, Pattern pattern)
    {
        this.name = name;
        this.provides = provides;
        this.startsWith = startsWith;
        this.pattern = pattern;
    }


    public abstract T get(S start, EventData data) throws BailException;


    public String toString(IDataProvider<S> startDP)
    {
        return startDP + "_" + name;
    }

    public DataProvider.IDataParser<T, S> parser()
    {
        return new Parser();
    }
    
    public IDataProvider<T> provider(IDataProvider<S> startDP)
    {
        return new Provider(startDP);
    }
    
    public class Provider implements IDataProvider<T>
    {
        public final IDataProvider<S> startDP;

        public Provider(IDataProvider<S> startDP)
        {
            this.startDP = startDP;
        }


        @Override
        public T get(EventData data) throws BailException {
            S start = startDP.get(data);
            if (start == null) return null;

            return Property.this.get(start, data);
        }

        @Override
        public Class<T> provides() {
            return Property.this.provides;
        }

        @Override
        public String toString() {
            return Property.this.toString(startDP);
        }
    }

    public class Parser implements DataProvider.IDataParser<T, S>
    {
        @Override
        public IDataProvider<T> parse(EventInfo info, IDataProvider<S> startDP, Matcher m, StringMatcher sm) {
            return provider(startDP);
        }
    }
}
