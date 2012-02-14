package com.ModDamage.Routines;

import java.util.ArrayList;
import java.util.List;

import com.ModDamage.EventInfo.EventData;

public class Routines
{
	public List<Routine> routines;
	
	public Routines()
	{
		this(new ArrayList<Routine>());
	}
	
	public Routines(List<Routine> routines)
	{
		this.routines = routines;
	}
	
	public void run(EventData data)
	{
		if (routines != null)
			for (Routine routine : routines)
				routine.run(data);
	}

	public boolean isEmpty()
	{
		return routines == null || routines.isEmpty();
	}
}
