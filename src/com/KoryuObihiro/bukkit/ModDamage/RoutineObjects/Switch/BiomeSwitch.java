package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine.EntitySingleTraitSwitchRoutine;

public class BiomeSwitch extends EntitySingleTraitSwitchRoutine<Biome>
{
	public BiomeSwitch(String configString, EntityReference entityReference, List<String> switchCases, List<Object> nestedContents)
	{
		super(configString, switchCases, nestedContents, ModDamageElement.GENERIC, entityReference);//XXX Optimization - check for generic first?
	}
	@Override
	protected Biome getRelevantInfo(TargetEventInfo eventInfo){ return getRelevantEntity(eventInfo).getLocation().getBlock().getBiome();}
	@Override
	protected boolean compare(Biome info_event, Collection<Biome> info_case){ return info_case.contains(info_event);}
	@Override
	protected Collection<Biome> matchCase(String switchCase){ return AliasManager.matchBiomeAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("(\\w+)\\.biome", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public BiomeSwitch getNew(Matcher matcher, List<String> switchCases, List<Object> nestedContents)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null)
				return new BiomeSwitch(matcher.group(), reference, switchCases, nestedContents);
			return null;
		}
	}
}
