package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.Aliasing.TypeAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class EntityType extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.type\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	final DataRef<Entity> entityRef;
	final DataRef<ModDamageElement> entityElementRef;
	final Collection<ModDamageElement> elements;
	public EntityType(DataRef<Entity> entityRef, DataRef<ModDamageElement> entityElementRef, Collection<ModDamageElement> elements)
	{ 
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.elements = elements;
	}
	@Override
	public boolean evaluate(EventData data)
	{
		ModDamageElement entityElement = entityElementRef.get(data);
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
		public EntityType getNew(Matcher matcher, EventInfo info)
		{
			Collection<ModDamageElement> elements = TypeAliaser.match(matcher.group(2));
			String name = matcher.group(1).toLowerCase();
			DataRef<Entity> entityRef = info.get(Entity.class, name);
			DataRef<ModDamageElement> entityElementRef = info.get(ModDamageElement.class, name);
			if(!elements.isEmpty())
				return new EntityType(entityRef, entityElementRef, elements);
			return null;
		}
	}
}
