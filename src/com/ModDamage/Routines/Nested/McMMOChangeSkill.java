package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.SkillType;

public class McMMOChangeSkill extends NestedRoutine
{	
	private final DataRef<Entity> entityRef;
	private final DynamicInteger skill_level;
	protected final SkillType skillType;
	protected final boolean isAdditive;
	protected McMMOChangeSkill(String configString, DataRef<Entity> entityRef, DynamicInteger skill_level, SkillType skillType, boolean isAdditive)
	{
		super(configString);
		this.entityRef = entityRef;
		this.skill_level = skill_level;
		this.skillType = skillType;
		this.isAdditive = isAdditive;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(Integer.class, "skill_level", "-default");
	
	@Override
	public void run(EventData data)
	{
		Entity entity = entityRef.get(data);
		if(entity instanceof Player)
		{
			Player player = (Player)entityRef.get(data);
			mcMMO mcMMOplugin = ExternalPluginManager.getMcMMOPlugin();
			if(mcMMOplugin != null)
			{
				EventData myData = myInfo.makeChainedData(data, 0);
				
				mcMMOplugin.getPlayerProfile(player).modifyskill(skillType, 
						skill_level.getValue(myData) + (isAdditive?mcMMOplugin.getPlayerProfile(player).getSkillLevel(skillType):0));
			}
		}
	}

	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*)effect\\.(set|add)skill\\.(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public McMMOChangeSkill getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			for(SkillType skillType : SkillType.values())
				if(matcher.group(3).equalsIgnoreCase(skillType.name()))
				{
					DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());

					EventInfo einfo = info.chain(myInfo);
					Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
					DynamicInteger skill_level = DynamicInteger.getNew(routines, einfo);
					
					if(entityRef != null)
						return new McMMOChangeSkill(matcher.group(), entityRef, skill_level, skillType, matcher.group(2).equalsIgnoreCase("add"));
				}
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid McMMO skill \"" + matcher.group(3) + "\"");
			return null;
		}
	}
}
