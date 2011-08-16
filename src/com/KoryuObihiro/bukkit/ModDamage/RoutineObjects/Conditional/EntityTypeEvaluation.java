package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityTypeEvaluation extends EntityConditionalStatement<List<ModDamageElement>>
{
	public EntityTypeEvaluation(boolean inverted, boolean forAttacker, List<ModDamageElement> value)
	{ 
		super(inverted, forAttacker, value);
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo)
	{
		for(ModDamageElement element : value)
			if((shouldGetAttacker(eventInfo)?((AttackerEventInfo)eventInfo).element_attacker:eventInfo.element_target).matchesType(element))
				return true;
		return false;
	}
	@Override
	protected List<ModDamageElement> getRelevantInfo(TargetEventInfo eventInfo){ return null;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityTypeEvaluation.class, Pattern.compile("(!)?(\\w+)\\.type\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityTypeEvaluation getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<ModDamageElement> elements = ModDamage.matchElementAlias(matcher.group(3));
			if(!elements.isEmpty())
				return new EntityTypeEvaluation(matcher.group(1) != null, (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false, elements);
		}
		return null;
	}
}
