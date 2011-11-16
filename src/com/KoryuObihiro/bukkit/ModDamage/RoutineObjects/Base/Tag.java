package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Tag extends Routine
{
	private final String tag;
	private final EntityReference entityReference;
	private final boolean setting;
	protected Tag(String configString, String tag, EntityReference entityReference, boolean setting)
	{
		super(configString);
		this.tag = tag;
		this.entityReference = entityReference;
		this.setting = setting;
	}

	@Override
	public void run(TargetEventInfo eventInfo)
	{
		if(entityReference.getEntity(eventInfo) != null)
		{
			if(setting)
				ModDamage.getTagger().addTag(tag, entityReference.getEntity(eventInfo));
			else ModDamage.getTagger().removeTag(tag, entityReference.getEntity(eventInfo));
		}
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(un)?tag\\.(\\w+)\\.(\\w+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Tag getNew(Matcher matcher)
		{
			if(EntityReference.isValid(matcher.group(2)))
				return new Tag(matcher.group(), matcher.group(3).toLowerCase(), EntityReference.match(matcher.group(2)), matcher.group(1) == null);
			return null;
		}
	}
}
