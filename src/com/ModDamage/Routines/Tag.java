package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.PluginConfiguration.OutputPreset;

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
		Entity entity = entityReference.getEntity(eventInfo);
		if(entity != null)
		{
			if(setting)
			{
				int value = defaultValue;
				if (integer != null) value = integer.getValue(eventInfo);
				ModDamage.getTagger().addTag(entity, tag, value);
			}
			else
				ModDamage.getTagger().removeTag(entity, tag);
		}
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(un)?tag\\.([^.]+)\\.([^.]+)(?:\\.(.+))?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
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
