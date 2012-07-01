package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ExternalPluginManager;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Routines.Routines;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.SkillType;

public class McMMOChangeSkill extends NestedRoutine
{	
	private final DataRef<Player> playerRef;
	private final IntegerExp skill_level;
	protected final SkillType skillType;
	protected final boolean isAdditive;
	protected McMMOChangeSkill(String configString, DataRef<Player> playerRef, IntegerExp skill_level, SkillType skillType, boolean isAdditive)
	{
		super(configString);
		this.playerRef = playerRef;
		this.skill_level = skill_level;
		this.skillType = skillType;
		this.isAdditive = isAdditive;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(IntRef.class, "skill_level", "-default");
	
	@Override
	public void run(EventData data) throws BailException
	{
		Player player = playerRef.get(data);
		mcMMO mcMMOplugin = ExternalPluginManager.getMcMMOPlugin();
		if(mcMMOplugin != null)
		{
			EventData myData = myInfo.makeChainedData(data, new IntRef(0));
			
			mcMMOplugin.getPlayerProfile(player).modifyskill(skillType, 
					skill_level.getValue(myData) + (isAdditive?mcMMOplugin.getPlayerProfile(player).getSkillLevel(skillType):0));
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
					DataRef<Player> playerRef = info.get(Player.class, matcher.group(1).toLowerCase());

					EventInfo einfo = info.chain(myInfo);
					Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
					IntegerExp skill_level = IntegerExp.getNew(routines, einfo);
					
					if(playerRef != null)
						return new McMMOChangeSkill(matcher.group(), playerRef, skill_level, skillType, matcher.group(2).equalsIgnoreCase("add"));
				}
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid McMMO skill \"" + matcher.group(3) + "\"");
			return null;
		}
	}
}
