package com.ModDamage.Routines.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.TargetEventInfo;

public class EntityTagged extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.istagged\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	final EntityReference entityReference;
	private final String tag;
	public EntityTagged(EntityReference entityReference, String tag)
	{
		this.entityReference = entityReference;
		this.tag = tag;
	}

	@Override
	public boolean evaluate(TargetEventInfo eventInfo)
	{
		return entityReference.getEntity(eventInfo) != null && ModDamage.getTagger().isTagged(entityReference.getEntity(eventInfo), tag);
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EntityTagged getNew(Matcher matcher)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null)
				return new EntityTagged(reference, matcher.group(2).toLowerCase());
			return null;
		}
	}
}
