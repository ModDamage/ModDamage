package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class EntityTagged extends EntityConditionalStatement
{
	private final String tag;
	public EntityTagged(boolean inverted, EntityReference entityReference, String tag)
	{
		super(inverted, entityReference);
		this.tag = tag;
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{
		return entityReference.getEntity(eventInfo) != null && ModDamage.getTagger().isTagged(entityReference.getEntity(eventInfo), tag);
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)(\\w+)\\.istagged\\.(\\w+)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public EntityTagged getNew(Matcher matcher)
		{
			if(EntityReference.isValid(matcher.group(2)))
				return new EntityTagged(matcher.group(1).equals("!"), EntityReference.match(matcher.group(2)), matcher.group(3).toLowerCase());
			return null;
		}
	}
}
