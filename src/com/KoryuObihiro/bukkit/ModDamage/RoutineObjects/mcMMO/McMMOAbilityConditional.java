package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.mcMMO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.gmail.nossr50.mcMMO;

public class McMMOAbilityConditional extends McMMOConditionalStatement
{
	protected final McMMOAbility ability;
	protected McMMOAbilityConditional(boolean inverted, EntityReference entityReference, McMMOAbility ability) 
	{
		super(inverted, entityReference);
		this.ability = ability;
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo, mcMMO mcMMOplugin, Player player)
	{
		return ability.isActivated(mcMMOplugin, player);
	}
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(McMMOAbilityConditional.class, Pattern.compile("(!?)(\\w+).hasactive.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static McMMOAbilityConditional getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			McMMOAbility mcMMOability = null;
			for(McMMOAbility ability : McMMOAbility.values())
				if(matcher.group(3).equalsIgnoreCase(ability.name()))
					mcMMOability = ability;
			if(mcMMOability == null) ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid McMMO ability \"" + matcher.group(3) + "\"", LoadState.FAILURE);
			if(EntityReference.isValid(matcher.group(2)) & mcMMOability != null)
				return new McMMOAbilityConditional(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), mcMMOability);
		}
		return null;
	}
}
