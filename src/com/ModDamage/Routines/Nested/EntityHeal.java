package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;

public class EntityHeal extends NestedRoutine
{
	private final DataRef<LivingEntity> entityRef;
	private final DynamicInteger heal_amount;
	
	public EntityHeal(String configString, DataRef<LivingEntity> entityRef, DynamicInteger heal_amount)
	{
		super(configString);
		this.entityRef = entityRef;
		this.heal_amount = heal_amount;
	}

	static final EventInfo myInfo = new SimpleEventInfo(IntRef.class, "heal_amount", "-default");
	
	@Override
	public void run(EventData data) throws BailException
	{
		EventData myData = myInfo.makeChainedData(data, new IntRef(0));
			
		LivingEntity entity = (LivingEntity)entityRef.get(data);
		entity.setHealth(Math.min(entity.getHealth() + heal_amount.getValue(myData), entity.getMaxHealth()));
	}

	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*)effect\\.heal", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{	
		@Override
		public EntityHeal getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			DataRef<LivingEntity> entityRef = info.get(LivingEntity.class, name);

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			DynamicInteger heal_amount = DynamicInteger.getNew(routines, einfo);
			if(entityRef != null)
				return new EntityHeal(matcher.group(), entityRef, heal_amount);
			return null;
		}
	}
}
