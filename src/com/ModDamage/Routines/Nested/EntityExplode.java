package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Routines.Routines;

public class EntityExplode extends NestedRoutine
{
	private final IDataProvider<Entity> entityDP;
	private final IDataProvider<Integer> strength;
	
	public EntityExplode(String configString, IDataProvider<Entity> entityDP, IDataProvider<Integer> strength)
	{
		super(configString);
		this.entityDP = entityDP;
		this.strength = strength;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(Integer.class, "strength", "-default");
	
	@Override
	public void run(EventData data) throws BailException
	{
		Entity entity = entityDP.get(data);
		
		EventData myData = myInfo.makeChainedData(data, 0);
		entity.getWorld().createExplosion(entity.getLocation(), strength.get(myData)/10.0f);
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*)effect\\.explode", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{	
		@Override
		public EntityExplode getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			IDataProvider<Entity> entityDP = DataProvider.parse(info, Entity.class, matcher.group(1));

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if (routines == null) return null;
			
			IDataProvider<Integer> strength = IntegerExp.getNew(routines, einfo);
			
			if(entityDP != null && strength != null)
				return new EntityExplode(matcher.group(), entityDP, strength);
			
			return null;
		}
	}
}
