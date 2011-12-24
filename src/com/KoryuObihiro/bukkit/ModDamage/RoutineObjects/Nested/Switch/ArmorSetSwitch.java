package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Switch;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.SwitchRoutine.EntitySingleTraitSwitchRoutine;

public class ArmorSetSwitch extends EntitySingleTraitSwitchRoutine<ArmorSet>
{
	public ArmorSetSwitch(String configString, EntityReference entityReference, List<String> switchCases, List<Object> nestedContents)
	{ 
		super(configString, switchCases, nestedContents, ModDamageElement.PLAYER, entityReference);
	}

	@Override
	protected ArmorSet getRelevantInfo(TargetEventInfo eventInfo)
	{ 
		return entityReference.getArmorSet(eventInfo);
	}
	@Override
	protected boolean compare(ArmorSet info_event, Collection<ArmorSet> info_case)
	{
		for(ArmorSet armorSet : info_case)
			if(armorSet.equals(info_event))
				return true;
		return false;
	}
	@Override
	protected Collection<ArmorSet> matchCase(String switchCase)
	{ 
		Collection<ArmorSet> armorSet = AliasManager.matchArmorAlias(switchCase);
		return (armorSet.isEmpty()?null:armorSet);
	}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("(\\w+)\\.wearing(only)?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public ArmorSetSwitch getNew(Matcher matcher, List<String> switchCases, List<Object> nestedContents)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null)
				return new ArmorSetSwitch(matcher.group(), reference, switchCases, nestedContents);
			return null;
		}
	}
}
