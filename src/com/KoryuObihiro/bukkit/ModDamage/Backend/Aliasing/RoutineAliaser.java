package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class RoutineAliaser extends Aliaser<Routine> 
{
	private static final long serialVersionUID = -2744471820826321788L;

	public RoutineAliaser() 
	{
		super("Routine");
	}

	@Override
	protected Routine matchNonAlias(String key) 
	{
		Routine routine = null;
		for(Pattern pattern : ModDamage.registeredBaseRoutines.keySet())
		{
			Matcher matcher = pattern.matcher(key);
			if(matcher.matches())
			{
				try
				{
					routine = (Routine)ModDamage.registeredBaseRoutines.get(pattern).invoke(null, matcher);
					break;
				}
				catch(Exception e){ e.printStackTrace();}
			}
		}
		return routine;
	}

	@Override
	protected String getObjectName(Routine routine){ return routine.getClass().getSimpleName();}

}
