package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Switch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;

public class EntityTypeSwitch extends EntitySwitchCalculation<DamageElement>
{
	private DamageElement type;
	public EntityTypeSwitch(boolean forAttacker, DamageElement type, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		super(forAttacker, switchStatements);
		this.type = type;
	}

	@Override
	protected DamageElement getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.element_attacker:eventInfo.element_target).getType();}

	@Override
	protected DamageElement getRelevantInfo(SpawnEventInfo eventInfo){ return eventInfo.element.getType();}

	@Override
	protected DamageElement matchCase(String switchCase) 
	{
		DamageElement element = DamageElement.matchElement(switchCase);
		if(type != null && !type.equals(DamageElement.GENERIC) && !element.equals(type) && element != null)
		{
			DamageElement temp = element.getType();
			while(true)
			{
				if(temp.equals(type)) break;
				temp = temp.getType();
				if(temp.equals(DamageElement.GENERIC)) return null;
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
		boolean forAttacker = matcher.group(1).equalsIgnoreCase("attacker");
		try
		{
			routine = new EntityTypeSwitch(forAttacker, DamageElement.matchElement(matcher.group(2)), switchStatements);
		}
		catch(IndexOutOfBoundsException e)
		{
			routine = new EntityTypeSwitch(forAttacker, DamageElement.GENERIC, switchStatements);
		}
		return (routine.isLoaded?routine:null);
	}
}
