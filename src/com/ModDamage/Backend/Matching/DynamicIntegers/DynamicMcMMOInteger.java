package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.SkillType;

public class DynamicMcMMOInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("("+ Utils.joinBy("|", EntityReference.values()) +")_SKILL_(\\w+)", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm)
					{
						try
						{
							DynamicInteger integer = new DynamicMcMMOInteger(
									EntityReference.valueOf(matcher.group(1).toUpperCase()), 
									SkillType.valueOf(matcher.group(2).toUpperCase()));
							sm.accept();
							return integer;
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
	protected final EntityReference entityReference;
	
	DynamicMcMMOInteger(EntityReference reference, SkillType skillType)
	{
		this.entityReference = reference;
		this.skillType = skillType;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
		{
			mcMMO mcMMOplugin = ExternalPluginManager.getMcMMOPlugin();
			if(mcMMOplugin != null)
				return mcMMOplugin.getPlayerProfile(((Player)entityReference.getEntity(eventInfo))).getSkillLevel(skillType);
		}
		return 0;
	}
	
	@Override
	public String toString()
	{
		return entityReference.name().toLowerCase() + "_skill_" + skillType.name().toLowerCase();
	}
}
