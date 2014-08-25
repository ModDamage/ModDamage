package com.ModDamage.Expressions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.Function.BlockFunction;
import com.ModDamage.Expressions.Function.DistanceFunction;
import com.ModDamage.Expressions.Function.IntFunction;
import com.ModDamage.Expressions.Function.LocFunction;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Variables.Number.EnchantmentInt;
import com.ModDamage.Variables.Number.ItemEnchantmentInt;
import com.ModDamage.Variables.Number.LocalNum;
import com.ModDamage.Variables.Number.NegativeNum;
import com.ModDamage.Variables.Number.NumberOp;
import com.ModDamage.Variables.Number.PotionEffectInt;
import com.ModDamage.Variables.Number.RoutinesNum;

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
    public static IDataProvider<Number> parse(ScriptLine scriptLine, StringMatcher sm, EventInfo info) {
        Matcher m = sm.matchFront(InterpolatedString.interpolationStartPattern);
        if (m != null) {
            IDataProvider<Number> numberDP = DataProvider.parse(scriptLine, info, Number.class, sm.spawn(), false, true, InterpolatedString.interpolationEndPattern);
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

		com.ModDamage.External.mcMMO.PlayerInt.register();
		com.ModDamage.External.mcMMO.PlayerSkillInt.register();
	}
}