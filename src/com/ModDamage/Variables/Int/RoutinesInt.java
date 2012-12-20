package com.ModDamage.Variables.Int;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.ISettableDataProvider;
import com.ModDamage.Routines.Routines;

public class RoutinesInt implements IDataProvider<Integer>
{
	private final Routines routines;
	private final ISettableDataProvider<Integer> defaultDP;
	
	public RoutinesInt(Routines routines, EventInfo info)
	{
		this.routines = routines;
		this.defaultDP = info.get(Integer.class, "-default");
	}
	
	@Override
	public Integer get(EventData data) throws BailException
	{
		routines.run(data);
		
		return defaultDP.get(data);
	}

	@Override
	public Class<Integer> provides() { return Integer.class; }
	
	@Override
	public String toString()
	{
		return "<routines>"; //TODO Make this a bit better?
	}
}
