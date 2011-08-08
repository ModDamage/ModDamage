package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class PlayerWielding extends EntityConditionalStatement<List<Material>> 
{
	public PlayerWielding(boolean inverted, boolean forAttacker, List<Material> materials)
	{  
		super(inverted, forAttacker, materials);
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo){ return value.contains((forAttacker && eventInfo instanceof AttackerEventInfo)?((AttackerEventInfo)eventInfo).materialInHand_attacker:eventInfo.materialInHand_target);}
	@Override
	public List<Material> getRelevantInfo(TargetEventInfo eventInfo){ return null;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, PlayerWielding.class, Pattern.compile("(!)?" + ModDamage.entityRegex + "wielding\\." + ModDamage.materialRegex, Pattern.CASE_INSENSITIVE));
	}	
	
	public static PlayerWielding getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<Material> matchedItems = ModDamage.matchItemAlias(matcher.group(3));
			if(!matchedItems.isEmpty())
				return new PlayerWielding(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"), matchedItems);
		}
		return null;
	}
}
