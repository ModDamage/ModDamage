package com.moddamage.variables.number;

import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.parsing.ISettableDataProvider;
import com.moddamage.routines.Routines;

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
