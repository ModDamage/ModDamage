package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class EntityTypeSwitch extends EntitySwitchRoutine<ModDamageElement>
{
	public EntityTypeSwitch(boolean forAttacker, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		super(forAttacker, switchStatements);
	}

	@Override
	protected ModDamageElement getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.element_attacker:eventInfo.element_target).getType();}

	@Override
	protected ModDamageElement getRelevantInfo(SpawnEventInfo eventInfo){ return eventInfo.element.getType();}

	@Override
	protected ModDamageElement matchCase(String switchCase){ return ModDamageElement.matchElement(switchCase);}
	
	public static void register(RoutineUtility routineUtility)
	{
		SwitchRoutine.registerStatement(routineUtility, EntityTypeSwitch.class, Pattern.compile(RoutineUtility.entityPart + "type(?:\\.(" + RoutineUtility.elementRegex + "))?", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityTypeSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		EntityTypeSwitch routine = null;
		if(matcher != null && switchStatements != null)
		{
			boolean forAttacker = matcher.group(1).equalsIgnoreCase("attacker");
			String typeString = matcher.group(2);
			ModDamageElement element = (typeString != null?ModDamageElement.matchElement(typeString):ModDamageElement.GENERIC);
			if(element != null) routine = new EntityTypeSwitch(forAttacker, switchStatements);
		}
		return routine;
	}
}
