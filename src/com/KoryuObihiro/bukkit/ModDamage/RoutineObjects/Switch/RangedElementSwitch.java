package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ProjectileEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.RangedElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class RangedElementSwitch extends SwitchRoutine<RangedElement>
{
	public RangedElementSwitch(String configString, LinkedHashMap<String, List<Routine>> switchLabels)
	{
		super(configString, switchLabels);
	}
	@Override
	protected RangedElement getRelevantInfo(TargetEventInfo eventInfo)
	{ 
		return (eventInfo instanceof ProjectileEventInfo)?((ProjectileEventInfo)eventInfo).rangedElement:null;
	}
	@Override
	protected RangedElement matchCase(String switchCase){ return RangedElement.matchElement(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerStatement(RangedElementSwitch.class, Pattern.compile("switch\\.event\\.rangedElement", Pattern.CASE_INSENSITIVE));
	}
	
	public static RangedElementSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		if(matcher != null && switchStatements != null)
		{
			return new RangedElementSwitch(matcher.group(), switchStatements);
		}
		return null;
	}
}
