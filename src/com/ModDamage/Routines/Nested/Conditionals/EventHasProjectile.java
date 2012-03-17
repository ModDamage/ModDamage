package com.ModDamage.Routines.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Projectile;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class EventHasProjectile extends Conditional 
{
	public static final Pattern pattern = Pattern.compile("event\\.hasprojectile", Pattern.CASE_INSENSITIVE);
	
	private final DataRef<Projectile> projectileRef;
	
	protected EventHasProjectile(String configString, DataRef<Projectile> projectileRef)
	{
		super(configString);
		this.projectileRef = projectileRef;
	}
	
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		return projectileRef.get(data) != null;
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EventHasProjectile getNew(Matcher matcher, EventInfo info)
		{
			DataRef<Projectile> projectileRef = info.get(Projectile.class, "projectile");
			return new EventHasProjectile(matcher.group(), projectileRef);
		}
	}
}
