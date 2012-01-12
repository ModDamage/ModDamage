package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

public class PlayerGroupEvaluation extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.group\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	final EntityReference entityReference;
	final Collection<String> groups;
	public PlayerGroupEvaluation(EntityReference entityReference, Collection<String> groups)
	{  
		this.entityReference = entityReference;
		this.groups = groups;
	}
	@Override
	public boolean evaluate(TargetEventInfo eventInfo) 
	{
		for(String group : entityReference.getGroups(eventInfo))
			if(groups.contains(group))
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
		public PlayerGroupEvaluation getNew(Matcher matcher)
		{
			Collection<String> matchedGroups = AliasManager.matchGroupAlias(matcher.group(2));
			if(!matchedGroups.isEmpty())
				return new PlayerGroupEvaluation(EntityReference.match(matcher.group(1)), matchedGroups);
			return null;
		}
	}
}
