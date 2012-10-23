package com.ModDamage.Expressions;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.Function.BlockLocFunction;
import com.ModDamage.Expressions.Function.DistanceFunction;
import com.ModDamage.Expressions.Function.IntFunction;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Variables.Int.*;

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
		BlockInt.register();
		EnchantmentInt.register();
		EntityInt.register();
		IntegerOpInt.register();
		ItemEnchantmentInt.register();
		ItemInt.register();
		LocationInt.register();
		NegativeInt.register();
		PlayerInt.register();
		PotionEffectInt.register();
		ServerInt.register();
		WorldInt.register();
		WorldTagInt.register();

		com.ModDamage.External.mcMMO.PlayerInt.register();
		com.ModDamage.External.mcMMO.PlayerSkillInt.register();
	}
}