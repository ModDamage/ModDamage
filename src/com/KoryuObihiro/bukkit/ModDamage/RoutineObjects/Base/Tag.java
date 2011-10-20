package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageTag;
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
				ModDamageTag.addTag(tag, entityReference.getEntity(eventInfo));
			else ModDamageTag.removeTag(tag, entityReference.getEntity(eventInfo));
		}
	}

	public static void register()
	{
		Routine.registerBase(Tag.class, Pattern.compile("(un)?tag\\.(\\w+)\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static Tag getNew(Matcher matcher)
	{
		if(matcher != null && EntityReference.isValid(matcher.group(2)))
		{
			ModDamageTag.generateTag(matcher.group(3));
			return new Tag(matcher.group(), matcher.group(3), EntityReference.match(matcher.group(2)), matcher.group(1) == null);
		}
		return null;
	}
}
