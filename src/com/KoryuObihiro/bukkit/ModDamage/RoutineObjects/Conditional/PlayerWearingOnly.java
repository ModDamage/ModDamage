package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class PlayerWearingOnly extends EntityConditionalStatement
{
	public PlayerWearingOnly(boolean inverted, EntityReference entityReference, List<ArmorSet> armorSetList)
	{  
		super(inverted, entityReference);
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo)
	{
		if((shouldGetAttacker(eventInfo)?((AttackerEventInfo)eventInfo).armorSet_attacker:eventInfo.armorSet_target) != null)
		{
			ArmorSet playerSet = (shouldGetAttacker(eventInfo)?((AttackerEventInfo)eventInfo).armorSet_attacker:eventInfo.armorSet_target);
			for(ArmorSet armorSet : value)
				if(armorSet.equals(playerSet))
					return true;
		}
		return false;
	}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, PlayerWearingOnly.class, Pattern.compile("(!?)(\\w+)\\.wearingonly\\.([\\*\\w]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerWearingOnly getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<ArmorSet> armorSetList = ModDamage.matchArmorAlias(matcher.group(3));
			if(!armorSetList.isEmpty() && TargetEventInfo.EntityReference.isValid(matcher.group(2)))
				return new PlayerWearingOnly(matcher.group(1).equalsIgnoreCase("!"), TargetEventInfo.EntityReference.match(matcher.group(2)), armorSetList);
		}
		return null;
	}
}
