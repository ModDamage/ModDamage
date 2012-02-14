package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;

public class EntityHeal extends NestedRoutine
{
	final DataRef<Entity> entityRef;
	final DataRef<ModDamageElement> entityElementRef;
	final DynamicInteger heal_amount;
	
	public EntityHeal(String configString, DataRef<Entity> entityRef, DataRef<ModDamageElement> entityElementRef, DynamicInteger heal_amount)
	{
		super(configString);
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.heal_amount = heal_amount;
	}

	static final EventInfo myInfo = new SimpleEventInfo(Integer.class, "heal_amount", "-default");
	
	@Override
	public void run(EventData data)
	{
		if(entityElementRef.get(data).matchesType(ModDamageElement.LIVING))
		{
			EventData myData = myInfo.makeChainedData(data, 0);
			
			LivingEntity entity = (LivingEntity)entityRef.get(data);
			entity.setHealth(Math.min(entity.getHealth() + heal_amount.getValue(myData), entity.getMaxHealth()));
		}
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
			DataRef<Entity> entityRef = info.get(Entity.class, name);
			DataRef<ModDamageElement> entityElementRef = info.get(ModDamageElement.class, name);

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			DynamicInteger heal_amount = DynamicInteger.getNew(routines, einfo);
			if(entityRef != null)
				return new EntityHeal(matcher.group(), entityRef, entityElementRef, heal_amount);
			return null;
		}
	}
}
