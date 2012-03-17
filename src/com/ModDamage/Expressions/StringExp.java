package com.ModDamage.Expressions;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Variables.Strings.EntityString;
import com.ModDamage.Variables.Strings.WorldString;

public abstract class StringExp
{
	public abstract String getString(EventData data) throws BailException;
	
	public static StringExp getNew(String string, EventInfo info)
	{
		StringExp str = WorldString.getNew(string, info);
		if (str != null) return str;
		
		str = EntityString.getNew(string, info);
		if (str != null) return str;
		
		return IntegerExp.getNew(string, info);
	}
	
	@Override
	public abstract String toString();
}