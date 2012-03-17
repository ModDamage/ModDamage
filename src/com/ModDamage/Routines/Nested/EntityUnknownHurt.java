package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Routines.Routines;

public class EntityUnknownHurt extends NestedRoutine
{
	private final DataRef<LivingEntity> entityRef;
	private final IntegerExp hurt_amount;
	
	public EntityUnknownHurt(String configString, DataRef<LivingEntity> entityRef, IntegerExp hurt_amount)
	{
		super(configString);
		this.entityRef = entityRef;
		this.hurt_amount = hurt_amount;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(IntRef.class, "hurt_amount", "-default");
	
	@Override
	public void run(EventData data) throws BailException
	{
		EventData myData = myInfo.makeChainedData(data, new IntRef(0));
		LivingEntity entity = entityRef.get(data);
		if(entity != null)
			entity.damage(hurt_amount.getValue(myData));
	}

	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*)effect\\.unknownhurt", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{	
		@Override
		public EntityUnknownHurt getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			DataRef<LivingEntity> entityRef = info.get(LivingEntity.class, name);

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			IntegerExp hurt_amount = IntegerExp.getNew(routines, einfo);
			
			if(entityRef != null)
				return new EntityUnknownHurt(matcher.group(), entityRef, hurt_amount);
			return null;
		}
	}
}
