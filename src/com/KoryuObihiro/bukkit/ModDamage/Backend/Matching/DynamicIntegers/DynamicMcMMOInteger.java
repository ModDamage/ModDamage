package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.Utils;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
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
					public DIResult getNewFromFront(Matcher matcher, String rest)
					{
						try
						{
							return new DIResult(new DynamicMcMMOInteger(
									EntityReference.valueOf(matcher.group(1).toUpperCase()), 
									SkillType.valueOf(matcher.group(2).toUpperCase())), rest);
						}
						catch (IllegalArgumentException e)
						{
							return null;
						}
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
		return entityReference.name().toLowerCase() + "_SKILL_" + skillType.name().toLowerCase();
	}
}
