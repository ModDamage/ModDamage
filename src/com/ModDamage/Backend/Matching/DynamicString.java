package com.ModDamage.Backend.Matching;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public abstract class DynamicString
{
	public abstract String getString(EventData data) throws BailException;
	
	public static DynamicString getNew(String string, EventInfo info)
	{
		DynamicString str = DynamicWorldString.getNew(string, info);
		if (str != null) return str;
		
		str = DynamicEntityString.getNew(string, info);
		if (str != null) return str;
		
		return DynamicInteger.getNew(string, info);
	}
	
	@Override
	public abstract String toString();
}