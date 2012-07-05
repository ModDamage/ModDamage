package com.ModDamage.External.mcMMO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Utils;
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
import com.ModDamage.Routines.Nested.NestedRoutine;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.SkillType;

public class ModifySkill extends NestedRoutine
{	
	enum ModifyType
	{
		ADDSKILLRAWXP {
				@Override
				public void modify(Player player, SkillType skillType, int value)
				{
					ExperienceAPI.addRawXP(player, skillType, value);
				}
			},
		ADDSKILLMULTIPLIEDXP {
				@Override
				public void modify(Player player, SkillType skillType, int value)
				{
					ExperienceAPI.addMultipliedXP(player, skillType, value);
				}
			},
		ADDSKILLXP {
				@Override
				public void modify(Player player, SkillType skillType, int value)
				{
					ExperienceAPI.addXP(player, skillType, value);
				}
			},
		ADDSKILLLEVEL {
				@Override
				public void modify(Player player, SkillType skillType, int value)
				{
					ExperienceAPI.addLevel(player, skillType, value, false);
				}
			},
		SETSKILLLEVEL {
				@Override
				public void modify(Player player, SkillType skillType, int value)
				{
					ExperienceAPI.setLevel(player, skillType, value);
				}
			},
		REMOVESKILLXP {
				@Override
				public void modify(Player player, SkillType skillType, int value)
				{
					ExperienceAPI.removeXP(player, skillType, value);
				}
			};

		public abstract void modify(Player player, SkillType skillType, int value);
	}
	
	private final DataRef<Player> playerRef;
	private final IntegerExp valueExp;
	protected final ModifyType modifyType;
	protected final SkillType skillType;
	protected ModifySkill(String configString, DataRef<Player> playerRef, IntegerExp valueExp, ModifyType modifyType, SkillType skillType)
	{
		super(configString);
		this.playerRef = playerRef;
		this.valueExp = valueExp;
		this.modifyType = modifyType;
		this.skillType = skillType;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(IntRef.class, "value", "-default");
	
	@Override
	public void run(EventData data) throws BailException
	{
		Player player = playerRef.get(data);
		EventData myData = myInfo.makeChainedData(data, new IntRef(0));
		
		int value = valueExp.getValue(myData);
		
		modifyType.modify(player, skillType, value);
	}

	public static void register()
	{
		try
		{
			NestedRoutine.registerRoutine(Pattern.compile("(\\w+?)(?:effect)?\\.("+Utils.joinBy("|", ModifyType.values())+")\\.("+Utils.joinBy("|", SkillType.values())+")", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
		}
		catch (NoClassDefFoundError e) {
			if (ExternalPluginManager.getMcMMOPlugin() != null)
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "McMMO has changed. Please notify the ModDamage developers.");
		}
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public ModifySkill getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			DataRef<Player> playerRef = info.get(Player.class, matcher.group(1).toLowerCase()); if (playerRef == null) return null;
			ModifyType modifyType = ModifyType.valueOf(matcher.group(2).toUpperCase());
			SkillType skillType = SkillType.valueOf(matcher.group(3).toUpperCase());
			
			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			IntegerExp valueExp = IntegerExp.getNew(routines, einfo);
			
			return new ModifySkill(matcher.group(), playerRef, valueExp, modifyType, skillType);
		}
	}
}
