package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.TypeAliaser;

public class EntityType extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.type\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	final EntityReference entityReference;
	final Collection<ModDamageElement> elements;
	public EntityType(EntityReference entityReference, Collection<ModDamageElement> elements)
	{ 
		this.entityReference = entityReference;
		this.elements = elements;
	}
	@Override
	public boolean evaluate(TargetEventInfo eventInfo)
	{
		ModDamageElement entityElement = entityReference.getElement(eventInfo);
		if(entityElement != null)
			for(ModDamageElement element : elements)
				if(entityElement.matchesType(element))
					return true;
		return false;
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EntityType getNew(Matcher matcher)
		{
			Collection<ModDamageElement> elements = TypeAliaser.match(matcher.group(2));
			if(!elements.isEmpty())
				return new EntityType(EntityReference.match(matcher.group(1)), elements);
			return null;
		}
	}
}
