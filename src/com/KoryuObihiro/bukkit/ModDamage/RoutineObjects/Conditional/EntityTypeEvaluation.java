package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalRoutine;

public class EntityTypeEvaluation extends EntityConditionalStatement
{
	final List<ModDamageElement> elements;
	public EntityTypeEvaluation(boolean inverted, EntityReference entityReference, List<ModDamageElement> elements)
	{ 
		super(inverted, entityReference);
		this.elements = elements;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo)
	{
		for(ModDamageElement element : elements)//TODO Reference for entity properties that aren't integers
			if(entityReference.getElement(eventInfo).matchesType(element))
				return true;
		return false;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityTypeEvaluation.class, Pattern.compile("(!?)(\\w+)\\.type\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityTypeEvaluation getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<ModDamageElement> elements = ModDamage.matchElementAlias(matcher.group(3));
			if(!elements.isEmpty())
				return new EntityTypeEvaluation(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), elements);
		}
		return null;
	}
}
