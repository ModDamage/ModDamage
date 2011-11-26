package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine.EntitySingleTraitSwitchRoutine;

public class WieldSwitch extends EntitySingleTraitSwitchRoutine<Material>
{
	public WieldSwitch(String configString, EntityReference entityReference, List<String> switchCases, List<Object> nestedContents) 
	{
		super(configString, switchCases, nestedContents, ModDamageElement.LIVING, entityReference);
	}
	@Override
	protected Material getRelevantInfo(TargetEventInfo eventInfo){ return entityReference.getMaterial(eventInfo);}
	@Override
	protected boolean compare(Material info_event, Collection<Material> info_case){ return info_case.contains(info_event);}
	@Override
	protected Collection<Material> matchCase(String switchCase){ return AliasManager.matchMaterialAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("(\\w+)\\.wielding", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public WieldSwitch getNew(Matcher matcher, List<String> switchCases, List<Object> nestedContents)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null)
				return new WieldSwitch(matcher.group(), reference, switchCases, nestedContents);
			return null;
		}
	}
}
