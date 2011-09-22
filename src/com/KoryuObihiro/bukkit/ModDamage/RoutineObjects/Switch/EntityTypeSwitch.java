package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityTypeSwitch extends LivingEntitySwitchRoutine<List<ModDamageElement>>
{
	public EntityTypeSwitch(String configString, EntityReference entityReference, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		super(configString, entityReference, switchStatements);
	}
	@Override
	protected List<ModDamageElement> getRelevantInfo(TargetEventInfo eventInfo){ return Arrays.asList(entityReference.getElement(eventInfo));}
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
	
	public static void register()
	{
		SwitchRoutine.registerStatement(EntityTypeSwitch.class, Pattern.compile("switch\\.(\\w+)\\.type", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityTypeSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		if(matcher != null && switchStatements != null && EntityReference.isValid(matcher.group(1)))
			return new EntityTypeSwitch(matcher.group(),  EntityReference.match(matcher.group(1)), switchStatements);
		return null;
	}
}
