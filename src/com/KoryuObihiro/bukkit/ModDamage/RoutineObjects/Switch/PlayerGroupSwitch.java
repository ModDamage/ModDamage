package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine.EntityMultipleTraitSwitchRoutine;

public class PlayerGroupSwitch extends EntityMultipleTraitSwitchRoutine<String>
{
	public PlayerGroupSwitch(String configString, List<String> switchCases, List<Object> nestedContents, EntityReference entityReference)
	{
		super(configString, switchCases, nestedContents, ModDamageElement.PLAYER, entityReference);
	}
	
	@Override
	protected List<String> getRelevantInfo(TargetEventInfo eventInfo){ return entityReference.getGroups(eventInfo);}
	
	@Override
	protected Collection<String> matchCase(String switchCase){ return AliasManager.matchGroupAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("(\\w+)\\.group", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public PlayerGroupSwitch getNew(Matcher matcher, List<String> switchCases, List<Object> nestedContents)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null)
				return new PlayerGroupSwitch(matcher.group(), switchCases, nestedContents, reference);
			return null;
		}
	}
	
}
