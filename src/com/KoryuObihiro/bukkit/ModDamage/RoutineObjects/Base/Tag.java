package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Tag extends Routine
{
	public static final int defaultValue = 0;
	
	protected final String tag;
	protected final EntityReference entityReference;
	protected final DynamicInteger integer;
	
	private final boolean setting;
	
	protected Tag(String configString, String tag, EntityReference entityReference, DynamicInteger integer)
	{
		super(configString);
		this.tag = tag;
		this.entityReference = entityReference;
		this.integer = integer;
		this.setting = true;
	}
	
	protected Tag(String configString, String tag, EntityReference entityReference)
	{
		super(configString);
		this.tag = tag;
		this.entityReference = entityReference;
		this.integer = null;
		this.setting = false;
	}

	@Override
	public void run(TargetEventInfo eventInfo)
	{
		if(entityReference.getEntity(eventInfo) != null)
		{
			if(setting)
				ModDamage.getTagger().addTag(tag, entityReference.getEntity(eventInfo), integer != null?integer.getValue(eventInfo):defaultValue);
			else ModDamage.getTagger().removeTag(tag, entityReference.getEntity(eventInfo));
		}
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(un)?tag\\.(.*)\\.(.*)(?:\\.(.*))?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Tag getNew(Matcher matcher)
		{
			if(matcher.group(3).matches("\\w+"))
			{
				EntityReference reference = EntityReference.match(matcher.group(2));
				if(reference != null)
				{
					if(matcher.group(1) == null)
					{
						if(matcher.group(4) == null)
						{
							ModDamage.addToLogRecord(OutputPreset.INFO, "Tag: " + matcher.group(2) + ", " + matcher.group(3) + ", " + defaultValue);
							return new Tag(matcher.group(), matcher.group(3).toLowerCase(), reference, null);
						}
						else
						{
							DynamicInteger integer = DynamicInteger.getNew(matcher.group(4));
							if(integer != null)
							{
								ModDamage.addToLogRecord(OutputPreset.INFO, "Tag: " + matcher.group(2) + ", " + matcher.group(3) + ", " + integer.toString());
								return new Tag(matcher.group(), matcher.group(3).toLowerCase(), reference, integer);
							}
						}
					}
					else
					{
						if(matcher.group(4) != null)
							ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, "Warning: Ignoring value \"" + matcher.group(4) + "\"; unused for untagging.");
						ModDamage.addToLogRecord(OutputPreset.INFO, "Untag: " + matcher.group(2) + ", " + matcher.group(3));
						return new Tag(matcher.group(), matcher.group(3).toLowerCase(), reference);
					}
				}
			}
			else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: tag \"" + matcher.group(3) + "\" should only be alphanumeric characters.");
			return null;
		}
	}
}
