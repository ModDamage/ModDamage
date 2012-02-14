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

public class EntityUnknownHurt extends NestedRoutine
{
	private final DataRef<Entity> entityRef;
	private final DataRef<ModDamageElement> entityElementRef;
	private final DynamicInteger hurt_amount;
	
	public EntityUnknownHurt(String configString, DataRef<Entity> entityRef, DataRef<ModDamageElement> entityElementRef, DynamicInteger hurt_amount)
	{
		super(configString);
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.hurt_amount = hurt_amount;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(Integer.class, "hurt_amount", "-default");
	
	@Override
	public void run(EventData data)
	{
		EventData myData = myInfo.makeChainedData(data, 0);
		if(entityElementRef.get(data).matchesType(ModDamageElement.LIVING))
			((LivingEntity)entityRef.get(data)).damage(hurt_amount.getValue(myData));
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
			DataRef<Entity> entityRef = info.get(Entity.class, name);
			DataRef<ModDamageElement> entityElementRef = info.get(ModDamageElement.class, name);

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			DynamicInteger hurt_amount = DynamicInteger.getNew(routines, einfo);
			
			if(entityRef != null)
				return new EntityUnknownHurt(matcher.group(), entityRef, entityElementRef, hurt_amount);
			return null;
		}
	}
}
