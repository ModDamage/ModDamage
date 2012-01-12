package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

public class EventHasProjectile extends Conditional 
{
	public static final Pattern pattern = Pattern.compile("event\\.hasprojectile", Pattern.CASE_INSENSITIVE);
	protected EventHasProjectile()
	{
	}
	@Override
	public boolean evaluate(TargetEventInfo eventInfo){ return eventInfo.type.equals(EntityReference.PROJECTILE);}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EventHasProjectile getNew(Matcher matcher)
		{
			return new EventHasProjectile();
		}
	}
}
