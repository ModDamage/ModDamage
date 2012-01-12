package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

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
			Collection<ModDamageElement> elements = AliasManager.matchTypeAlias(matcher.group(2));
			if(!elements.isEmpty())
				return new EntityType(EntityReference.match(matcher.group(1)), elements);
			return null;
		}
	}
}
