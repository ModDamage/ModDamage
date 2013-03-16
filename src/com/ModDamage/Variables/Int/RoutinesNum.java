package com.ModDamage.Variables.Int;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.ISettableDataProvider;
import com.ModDamage.Routines.Routines;

public class RoutinesNum implements IDataProvider<Number>
{
	private final Routines routines;
	private final ISettableDataProvider<Number> defaultDP;
	
	public RoutinesNum(Routines routines, EventInfo info)
	{
		this.routines = routines;
		this.defaultDP = info.get(Number.class, "-default");
	}
	
	@Override
	public Number get(EventData data) throws BailException
	{
		routines.run(data);
		
		return defaultDP.get(data);
	}

	@Override
	public Class<Number> provides() { return Number.class; }
	
	@Override
	public String toString()
	{
		return "<routines>"; //TODO Make this a bit better?
	}
}
