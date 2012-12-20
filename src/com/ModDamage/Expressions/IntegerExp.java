package com.ModDamage.Expressions;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.Function.BlockLocFunction;
import com.ModDamage.Expressions.Function.DistanceFunction;
import com.ModDamage.Expressions.Function.IntFunction;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routines;
import com.ModDamage.StringMatcher;
import com.ModDamage.Variables.Int.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class IntegerExp<From> extends DataProvider<Integer, From>
{
	protected IntegerExp(Class<From> wantStart, IDataProvider<From> startDP)
	{
		super(wantStart, startDP);
		defaultValue = 0;
	}
	
	public final Integer get(From from, EventData data) throws BailException
	{
		try
		{
			return myGet(from, data);
		}
		catch (Throwable t)
		{
			throw new BailException(this, t);
		}
	}
	protected abstract Integer myGet(From from, EventData data) throws BailException;

    public static final Pattern literalInteger = Pattern.compile("[0-9]+");

    /**
     * This parses either a literal number (123) or %{integer}
     * @param sm sm.spawn()
     * @param info The current EventInfo
     * @return The new Integer IDataProvider or null if parsing failed
     */
    public static IDataProvider<Integer> parse(StringMatcher sm, EventInfo info) {
        Matcher m = sm.matchFront(InterpolatedString.interpolationStartPattern);
        if (m != null) {
            IDataProvider<Integer> integerDP = DataProvider.parse(info, Integer.class, sm.spawn(), false, true, InterpolatedString.interpolationEndPattern);
            if (integerDP == null) return null;
            if (!sm.matchesFront(InterpolatedString.interpolationEndPattern)) return null;
            return integerDP;
        }

        m = sm.matchFront(literalInteger);
        if (m != null) {
            return new Constant(Integer.parseInt(m.group()));
        }

        return null;
    }
	
	@Override
	public Class<Integer> provides() { return Integer.class; }
	
	public static IDataProvider<Integer> getNew(Routines routines, EventInfo info) 
	{
		if(routines != null && !routines.isEmpty())
			return new RoutinesInt(routines, info);
		return null;
	}
	
	public static void registerAllIntegers()
	{
		IntFunction.register();
		BlockLocFunction.register();
		DistanceFunction.register();
		
		Constant.register();
		LocalInt.register();
		EnchantmentInt.register();
		IntegerOpInt.register();
		ItemEnchantmentInt.register();
		NegativeInt.register();
		PotionEffectInt.register();
		ServerInt.register();

		com.ModDamage.External.mcMMO.PlayerInt.register();
		com.ModDamage.External.mcMMO.PlayerSkillInt.register();
	}
}