package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.SkillType;

public class DynamicMcMMOInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("([a-z]+)_SKILL_(\\w+)", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						try
						{
							String name = matcher.group(1).toLowerCase();
							DataRef<Entity> entityRef = info.get(Entity.class, name);
							DataRef<ModDamageElement> entityElementRef = info.get(ModDamageElement.class, name);
							if (entityRef == null || entityElementRef == null) return null;
							
							return sm.acceptIf(new DynamicMcMMOInteger(
									entityRef, entityElementRef,
									SkillType.valueOf(matcher.group(2).toUpperCase())));
						}
						catch (IllegalArgumentException e) { }
						catch (NoClassDefFoundError e) {
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "McMMO has changed. Please notify the ModDamage developers.");
						}
						return null;
					}
				});
	}
	
	//protected static mcMMO mcMMOplugin;
	protected final SkillType skillType;
	protected final DataRef<Entity> entityRef;
	protected final DataRef<ModDamageElement> entityElementRef;
	
	DynamicMcMMOInteger(DataRef<Entity> entityRef, DataRef<ModDamageElement> entityElementRef, SkillType skillType)
	{
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.skillType = skillType;
	}
	
	@Override
	public int getValue(EventData data)
	{
		if(entityElementRef.get(data).matchesType(ModDamageElement.PLAYER))
		{
			mcMMO mcMMOplugin = ExternalPluginManager.getMcMMOPlugin();
			if(mcMMOplugin != null)
				return mcMMOplugin.getPlayerProfile(((Player)entityRef.get(data))).getSkillLevel(skillType);
		}
		return 0;
	}
	
	@Override
	public String toString()
	{
		return /*FIXME entityIndex.name().toLowerCase() +*/ "_skill_" + skillType.name().toLowerCase();
	}
}
