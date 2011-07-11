package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

public enum CalculationState
{
	CONTINUE, SKIP_ELSE, STOP;
	
	public static CalculationState matchState(String key)
	{
		for(CalculationState state : CalculationState.values())
			if(key.equalsIgnoreCase(state.name())) return state;
		return null;
	}
}
