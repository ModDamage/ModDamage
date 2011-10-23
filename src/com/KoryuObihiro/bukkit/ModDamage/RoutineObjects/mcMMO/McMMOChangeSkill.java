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

public class McMMOChangeSkill extends McMMOCalculationRoutine
{	
	private static final Pattern setSkillPattern = Pattern.compile("(\\w+)effect\\.(set|add)skill\\.(\\w+)", Pattern.CASE_INSENSITIVE);
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
		CalculationRoutine.registerCalculation(McMMOChangeSkill.class, setSkillPattern);
	}
	
	public static McMMOChangeSkill getNew(String configString, Object nestedContent)
	{
		if(configString != null && nestedContent != null)
		{
			Matcher matcher = setSkillPattern.matcher(configString);
			if(matcher.matches())
			{
				SkillType skillType = null;
				for(SkillType type : SkillType.values())
					if(matcher.group(3).equalsIgnoreCase(type.name()))
						skillType = type;
				if(skillType == null) ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid McMMO skill \"" + matcher.group(3) + "\"", LoadState.FAILURE);
			
				LoadState[] stateMachine = { LoadState.SUCCESS };
				List<Routine> routines = RoutineAliaser.parse(nestedContent, stateMachine);				
				if(EntityReference.isValid(matcher.group(1)) & skillType != null & stateMachine.equals(LoadState.SUCCESS))
					return new McMMOChangeSkill(configString, EntityReference.match(matcher.group(1)), DynamicInteger.getNew(routines), skillType, matcher.group(2).equalsIgnoreCase("add"));
			}
		}
		return null;
	}
}
