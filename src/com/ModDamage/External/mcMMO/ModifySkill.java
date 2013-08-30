package com.ModDamage.External.mcMMO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.Utils;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.NumberExp;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Routines.Nested.NestedRoutine;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.SkillType;

public class ModifySkill extends NestedRoutine
{	
	enum ModifyType
	{
		ADDSKILLRAWXP {
				@Override
				public void modify(Player player, SkillType skillType, int value)
				{
					ExperienceAPI.addRawXP(player, skillType.name(), value);
				}
			},
		ADDSKILLMULTIPLIEDXP {
				@Override
				public void modify(Player player, SkillType skillType, int value)
				{
					ExperienceAPI.addMultipliedXP(player, skillType.name(), value);
				}
			},
		ADDSKILLXP {
				@Override
				public void modify(Player player, SkillType skillType, int value)
				{
					ExperienceAPI.addXP(player, skillType.name(), value);
				}
			},
		ADDSKILLLEVEL {
				@Override
				public void modify(Player player, SkillType skillType, int value)
				{
					ExperienceAPI.addLevel(player, skillType.name(), value);
				}
			},
		SETSKILLLEVEL {
				@Override
				public void modify(Player player, SkillType skillType, int value)
				{
					ExperienceAPI.setLevel(player, skillType.name(), value);
				}
			},
		REMOVESKILLXP {
				@Override
				public void modify(Player player, SkillType skillType, int value)
				{
					ExperienceAPI.removeXP(player, skillType.name(), value);
				}
			};

		public abstract void modify(Player player, SkillType skillType, int value);
	}
	
	private final IDataProvider<Player> playerDP;
	private final IDataProvider<Number> valueExp;
	protected final ModifyType modifyType;
	protected final SkillType skillType;
	protected ModifySkill(String configString, IDataProvider<Player> playerDP, IDataProvider<Number> valueExp, ModifyType modifyType, SkillType skillType)
	{
		super(configString);
		this.playerDP = playerDP;
		this.valueExp = valueExp;
		this.modifyType = modifyType;
		this.skillType = skillType;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(Number.class, "value", "-default");
	
	@Override
	public void run(EventData data) throws BailException
	{
		Player player = playerDP.get(data);
		EventData myData = myInfo.makeChainedData(data, 0);
		
		Number v = valueExp.get(myData);
		if (v == null)
			return;
		
		int value = v.intValue();
		
		modifyType.modify(player, skillType, value);
	}

	public static void register()
	{
		try
		{
			NestedRoutine.registerRoutine(Pattern.compile("(\\w+?)(?:effect)?\\.("+Utils.joinBy("|", ModifyType.values())+")\\.("+Utils.joinBy("|", SkillType.values())+")", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
		}
		catch (NoClassDefFoundError e) {
//			if (ExternalPluginManager.getMcMMOPlugin() != null)
//				ModDamage.addToLogRecord(OutputPreset.FAILURE, "McMMO has changed. Please notify the ModDamage developers.");
		}
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public ModifySkill getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			IDataProvider<Player> playerDP = DataProvider.parse(info, Player.class, matcher.group(1)); if (playerDP == null) return null;
			ModifyType modifyType = ModifyType.valueOf(matcher.group(2).toUpperCase());
			SkillType skillType = SkillType.valueOf(matcher.group(3).toUpperCase());
			
			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			IDataProvider<Number> valueExp = NumberExp.getNew(routines, einfo);
			
			return new ModifySkill(matcher.group(), playerDP, valueExp, modifyType, skillType);
		}
	}
}
