package com.ModDamage.Expressions;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Variables.Int.Constant;
import com.ModDamage.Variables.Int.EnchantmentInt;
import com.ModDamage.Variables.Int.EntityInt;
import com.ModDamage.Variables.Int.EntityTagInt;
import com.ModDamage.Variables.Int.IntegerOpInt;
import com.ModDamage.Variables.Int.ItemEnchantmentInt;
import com.ModDamage.Variables.Int.ItemInt;
import com.ModDamage.Variables.Int.NegativeInt;
import com.ModDamage.Variables.Int.PlayerInt;
import com.ModDamage.Variables.Int.PotionEffectInt;
import com.ModDamage.Variables.Int.RoutinesInt;
import com.ModDamage.Variables.Int.ServerInt;
import com.ModDamage.Variables.Int.WorldInt;
import com.ModDamage.Variables.Int.WorldTagInt;

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
		Function.register();
		
		Constant.register();
		EnchantmentInt.register();
		EntityInt.register();
		EntityTagInt.register();
		IntegerOpInt.register();
		ItemEnchantmentInt.register();
		ItemInt.register();
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