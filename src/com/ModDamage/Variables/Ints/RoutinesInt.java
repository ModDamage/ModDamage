package com.ModDamage.Variables.Ints;

import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Routines.Routines;

public class RoutinesInt extends IntegerExp
{
	private final Routines routines;
	private final DataRef<IntRef> defaultRef;
	
	public RoutinesInt(Routines routines, EventInfo info)
	{
		this.routines = routines;
		this.defaultRef = info.get(IntRef.class, "-default");
	}
	
	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		routines.run(data);
		
		return defaultRef.get(data).value;
	}
	
	@Override
	public String toString()
	{
		return "<routines>";//TODO Make this a bit better?
	}
	
}
