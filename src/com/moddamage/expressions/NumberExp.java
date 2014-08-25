package com.moddamage.expressions;

import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.expressions.function.BlockFunction;
import com.moddamage.expressions.function.DistanceFunction;
import com.moddamage.expressions.function.IntFunction;
import com.moddamage.expressions.function.LocFunction;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.routines.Routines;
import com.moddamage.variables.number.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class NumberExp<From> extends DataProvider<Number, From>
{
	protected NumberExp(Class<From> wantStart, IDataProvider<From> startDP)
	{
		super(wantStart, startDP);
		defaultValue = 0;
	}
	
	public final Number get(From from, EventData data) throws BailException
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
	protected abstract Number myGet(From from, EventData data) throws BailException;

    public static final Pattern literalNumber = Pattern.compile("[0-9]+(\\.[0-9]+)?");

    /**
     * This parses either a literal number (123) or %{number}
     * @param sm sm.spawn()
     * @param info The current EventInfo
     * @return The new Number IDataProvider or null if parsing failed
     */
    public static IDataProvider<Number> parse(StringMatcher sm, EventInfo info) {
        Matcher m = sm.matchFront(InterpolatedString.interpolationStartPattern);
        if (m != null) {
            IDataProvider<Number> numberDP = DataProvider.parse(info, Number.class, sm.spawn(), false, true, InterpolatedString.interpolationEndPattern);
            if (numberDP == null) return null;
            if (!sm.matchesFront(InterpolatedString.interpolationEndPattern)) return null;
            return numberDP;
        }

        m = sm.matchFront(literalNumber);
        if (m != null) {
        	if (m.group(1) != null)
                return new LiteralNumber(Double.parseDouble(m.group()));
        	else
        		return new LiteralNumber(Integer.parseInt(m.group()));
        }

        return null;
    }
	
	@Override
	public Class<? extends Number> provides() { return Number.class; }
	
	public static IDataProvider<Number> getNew(Routines routines, EventInfo info) 
	{
		if(routines != null && !routines.isEmpty())
			return new RoutinesNum(routines, info);
		return null;
	}
	
	public static void registerAllNumbers()
	{
		IntFunction.register();
		LocFunction.register();
		BlockFunction.register();
		DistanceFunction.register();
		
		LiteralNumber.register();
		LocalNum.register();
		EnchantmentInt.register();
		NumberOp.register();
		ItemEnchantmentInt.register();
		NegativeNum.register();
		PotionEffectInt.register();

		com.moddamage.external.mcMMO.PlayerInt.register();
		com.moddamage.external.mcMMO.PlayerSkillInt.register();
	}
}