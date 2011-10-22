package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.mcMMO;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.SkillType;

public class McMMOSetSkill extends McMMOCalculationRoutine
{	
	private static final Pattern setSkillPattern = Pattern.compile("(\\w+)effect\\.setskill\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	protected final SkillType skillType;
	protected McMMOSetSkill(String configString, EntityReference entityReference, DynamicInteger value, SkillType skillType)
	{
		super(configString, value, entityReference);
		this.skillType = skillType;
	}

	@Override
	protected void applyEffect(Player player, int input, mcMMO mcMMOplugin)
	{
		mcMMOplugin.getPlayerProfile(player).modifyskill(skillType, input);
	}

	public static void register()
	{
		//Routine.registerBase(McMMOSetSkill.class, Pattern.compile("(\\w+)effect\\.setskill\\.(\\w+)\\." + IntegerMatch.dynamicIntegerPart, Pattern.CASE_INSENSITIVE));
		CalculationRoutine.registerCalculation(McMMOSetSkill.class, setSkillPattern);
	}
	
	public static McMMOSetSkill getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			SkillType skillType = null;
			for(SkillType skill : SkillType.values())
				if(matcher.group(2).equalsIgnoreCase(skill.name()))
					skillType = skill;
			DynamicInteger match = DynamicInteger.getNew(matcher.group(3));
			if(EntityReference.isValid(matcher.group(1)) && match != null && skillType != null);
				return new McMMOSetSkill(matcher.group(), EntityReference.match(matcher.group(1)), match, skillType);
		}
		return null;
	}
	
	public static McMMOSetSkill getNew(String configString, Object nestedContent)
	{
		if(configString != null && nestedContent != null)
		{
			Matcher matcher = setSkillPattern.matcher(configString);
			if(matcher.matches())
			{
				SkillType skillType = null;
				for(SkillType type : SkillType.values())
					if(matcher.group(2).equalsIgnoreCase(type.name()))
						skillType = type;
				if(skillType == null) ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid McMMO skill \"" + matcher.group(2) + "\"", LoadState.FAILURE);
			
				LoadState[] stateMachine = { LoadState.SUCCESS };
				List<Routine> routines = RoutineAliaser.parse(nestedContent, stateMachine);				
				if(EntityReference.isValid(matcher.group(1)) & skillType != null & stateMachine.equals(LoadState.SUCCESS))
					return new McMMOSetSkill(configString, EntityReference.match(matcher.group(1)), DynamicInteger.getNew(routines), skillType);
			}
		}
		return null;
	}
}
