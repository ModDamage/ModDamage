package com.ModDamage.Expressions;

import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Variables.String.EntityAsString;
import com.ModDamage.Variables.String.EntityString;
import com.ModDamage.Variables.String.IntAsString;
import com.ModDamage.Variables.String.PlayerString;
import com.ModDamage.Variables.String.WorldString;

public abstract class StringExp<From> extends DataProvider<String, From>
{
	protected StringExp(Class<From> wantStart, IDataProvider<From> startDP)
	{
		super(wantStart, startDP);
	}
	
	@Override
	public Class<String> provides() { return String.class; }
	
	public static void register()
	{
		EntityString.register();
		IntAsString.register();
		EntityAsString.register();
		PlayerString.register();
		WorldString.register();
	}
}