package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class EntityTypeSwitch extends EntitySwitchRoutine<List<ModDamageElement>, ModDamageElement>
{
	public EntityTypeSwitch(String configString, EntityReference entityReference, LinkedHashMap<String, Object> switchStatements)
	{
		super(configString, entityReference, switchStatements);
	}
	@Override
	protected ModDamageElement getRelevantInfo(TargetEventInfo eventInfo){ return entityReference.getElement(eventInfo);}
	@Override
	protected boolean compare(ModDamageElement info_event, List<ModDamageElement> info_case)
	{ //FIXME Not working with an alias?
		for(ModDamageElement element : info_case)
			if(info_event.matchesType(element))
				return true;
		return false;
	}
	@Override
	protected List<ModDamageElement> matchCase(String switchCase){ return ModDamage.matchElementAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerStatement(EntityTypeSwitch.class, Pattern.compile("switch\\.(\\w+)\\.type", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityTypeSwitch getNew(Matcher matcher, LinkedHashMap<String, Object> switchStatements)
	{
		if(matcher != null && switchStatements != null && EntityReference.isValid(matcher.group(1)))
			return new EntityTypeSwitch(matcher.group(),  EntityReference.match(matcher.group(1)), switchStatements);
		return null;
	}
}