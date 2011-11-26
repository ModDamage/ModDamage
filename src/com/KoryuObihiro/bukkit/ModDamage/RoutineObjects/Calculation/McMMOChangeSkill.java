package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.SkillType;

public class McMMOChangeSkill extends McMMOCalculationRoutine
{	
	protected final SkillType skillType;
	protected final boolean isAdditive;
	protected McMMOChangeSkill(String configString, EntityReference entityReference, DynamicInteger value, SkillType skillType, boolean isAdditive)
	{
		super(configString, value, entityReference);
		this.skillType = skillType;
		this.isAdditive = isAdditive;
	}

	@Override
	protected void applyEffect(Player player, int input, mcMMO mcMMOplugin)
	{
		mcMMOplugin.getPlayerProfile(player).modifyskill(skillType, input + (isAdditive?mcMMOplugin.getPlayerProfile(player).getSkillLevel(skillType):0));
	}

	public static void register()
	{
		CalculationRoutine.registerRoutine(Pattern.compile("(\\w+)effect\\.(set|add)skill\\.(\\w+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends CalculationRoutine.CalculationBuilder
	{
		@Override
		public McMMOChangeSkill getNew(Matcher matcher, DynamicInteger routines)
		{
			for(SkillType skillType : SkillType.values())
				if(matcher.group(3).equalsIgnoreCase(skillType.name()))
				{
					EntityReference reference = EntityReference.match(matcher.group(1));
					if(reference != null)
						return new McMMOChangeSkill(matcher.group(), reference, routines, skillType, matcher.group(2).equalsIgnoreCase("add"));
				}
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid McMMO skill \"" + matcher.group(3) + "\"");
			return null;
		}
	}
}
