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

public class EntityTypeSwitch extends EntitySwitchCalculation<ModDamageElement>
{
	private ModDamageElement type;
	public EntityTypeSwitch(boolean forAttacker, ModDamageElement type, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		super(forAttacker, switchStatements);
		this.type = type;
	}

	@Override
	protected ModDamageElement getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.element_attacker:eventInfo.element_target).getType();}

	@Override
	protected ModDamageElement getRelevantInfo(SpawnEventInfo eventInfo){ return eventInfo.element.getType();}

	@Override
	protected ModDamageElement matchCase(String switchCase) 
	{
		ModDamageElement element = ModDamageElement.matchElement(switchCase);
		if(type != null && !type.equals(ModDamageElement.GENERIC) && !element.equals(type) && element != null)
		{
			ModDamageElement temp = element.getType();
			while(true)
			{
				if(temp.equals(type)) break;
				temp = temp.getType();
				if(temp.equals(ModDamageElement.GENERIC)) return null;
			}
		}
		return element;
	}
	
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
			try
			{
				routine = new EntityTypeSwitch(forAttacker, ModDamageElement.matchElement(matcher.group(2)), switchStatements);
			}
			catch(IndexOutOfBoundsException e)
			{
				routine = new EntityTypeSwitch(forAttacker, ModDamageElement.GENERIC, switchStatements);
			}
			return (routine.isLoaded?routine:null);
		}
		return null;
	}
}
