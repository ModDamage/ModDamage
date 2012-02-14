package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.Backend.Aliasing.GroupAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class PlayerGroupEvaluation extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.group\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	final DataRef<Entity> entityRef;
	final Collection<String> groups;
	public PlayerGroupEvaluation(DataRef<Entity> entityRef, Collection<String> groups)
	{  
		this.entityRef = entityRef;
		this.groups = groups;
	}
	@Override
	public boolean evaluate(EventData data) 
	{
		Entity entity = entityRef.get(data);
		if (entity instanceof Player)
			for(String group : ExternalPluginManager.getPermissionsManager().getGroups((Player) entity))
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
		public PlayerGroupEvaluation getNew(Matcher matcher, EventInfo info)
		{
			Collection<String> matchedGroups = GroupAliaser.match(matcher.group(2));
			if(!matchedGroups.isEmpty())
				return new PlayerGroupEvaluation(info.get(Entity.class, matcher.group(1).toLowerCase()), matchedGroups);
			return null;
		}
	}
}
