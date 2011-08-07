package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class EntityTypeSwitch extends EntitySwitchRoutine<List<ModDamageElement>>
{
	public EntityTypeSwitch(boolean forAttacker, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		super(forAttacker, switchStatements);
	}
	@Override
	protected List<ModDamageElement> getRelevantInfo(TargetEventInfo eventInfo){ return Arrays.asList(shouldGetAttacker(eventInfo)?((AttackerEventInfo)eventInfo).element_attacker:eventInfo.element_target);}
	@Override
	protected boolean compare(List<ModDamageElement> info_1, List<ModDamageElement> info_2)
	{ //FIXME Not working with an alias?
		for(ModDamageElement element : info_2)
			if(info_1.get(0).matchesType(element))
				return true;
		return false;
	}
	@Override
	protected List<ModDamageElement> matchCase(String switchCase){ return ModDamage.matchElementAlias(switchCase);}
	
	public static void register(ModDamage routineUtility)
	{
		SwitchRoutine.registerStatement(routineUtility, EntityTypeSwitch.class, Pattern.compile(ModDamage.entityRegex + "type", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityTypeSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		if(matcher != null && switchStatements != null)
			return new EntityTypeSwitch(matcher.group(1).equalsIgnoreCase("attacker"), switchStatements);
		return null;
	}
}
