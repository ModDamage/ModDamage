package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Switch;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.SwitchRoutine.EntitySingleTraitSwitchRoutine;

public class EntityTypeSwitch extends EntitySingleTraitSwitchRoutine<ModDamageElement>
{
	public EntityTypeSwitch(String configString, EntityReference entityReference, List<String> switchCases, List<Object> nestedContents)
	{
		super(configString, switchCases, nestedContents, ModDamageElement.GENERIC, entityReference);
		
		for (int i = 1; i < this.switchCases.size(); i++)
		{
			Collection<ModDamageElement> elements2 = this.switchCases.get(i);
			for (int j = 0; j < i; j++)
			{
				Collection<ModDamageElement> elements1 = this.switchCases.get(j);
				
				ELEMENTLOOP: for (ModDamageElement element1 : elements1)
					for (ModDamageElement element2 : elements2)
						if (element2.matchesType(element1))
						{
							ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, "Case \""+switchCases.get(i)+"\" is matched first by case \""+switchCases.get(j)+"\" and "+ 
									((elements1.size()==1&&elements2.size()==1)?"will":"might") +" never get evaluated.");
							break ELEMENTLOOP;
						}
			}
		}
	}
	@Override
	protected ModDamageElement getRelevantInfo(TargetEventInfo eventInfo){ return entityReference.getElement(eventInfo);}
	@Override
	protected boolean compare(ModDamageElement info_event, Collection<ModDamageElement> info_case)
	{
		for(ModDamageElement element : info_case)
			if(info_event.matchesType(element))
				return true;
		return false;
	}
	@Override
	protected Collection<ModDamageElement> matchCase(String switchCase){ return AliasManager.matchTypeAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("(\\w+)\\.type", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public EntityTypeSwitch getNew(Matcher matcher, List<String> switchCases, List<Object> nestedContents)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null)
				return new EntityTypeSwitch(matcher.group(), reference, switchCases, nestedContents);
			return null;
		}
	}
}