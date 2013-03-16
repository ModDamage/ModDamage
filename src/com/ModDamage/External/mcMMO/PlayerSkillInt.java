package com.ModDamage.External.mcMMO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ExternalPluginManager;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.NumberExp;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.SkillType;

public class PlayerSkillInt extends NumberExp<Player>
{
	public static void register()
	{
		DataProvider.register(Number.class, Player.class, 
				Pattern.compile("_SKILL(|"+Utils.joinBy("|", SkillProperty.values())+")_(\\w+)", Pattern.CASE_INSENSITIVE),
				new IDataParser<Number, Player>()
				{
					@Override
					public IDataProvider<Number> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
					{
						String skillPropStr = m.group(1).toUpperCase();
						String skillTypeStr = m.group(2).toUpperCase();

						if (skillPropStr == "") skillPropStr = "LEVEL";

						SkillProperty skillProp;
						SkillType skillType;

						try
						{
							try
							{
								skillProp = SkillProperty.valueOf(skillPropStr);
							}
							catch (IllegalArgumentException e) {
								// SkillProperty.valueOf failed to find a match
								ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown skill property \""+skillPropStr+"\", valid values are: "+Utils.joinBy(", ", SkillProperty.values()));
								return null;
							}

							try
							{
								skillType = SkillType.valueOf(skillTypeStr);
							}
							catch (IllegalArgumentException e) {
								// SkillType.valueOf failed to find a match
								ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown skill type \""+skillTypeStr+"\", valid values are: "+Utils.joinBy(", ", SkillType.values()));
								return null;
							}

							return sm.acceptIf(new PlayerSkillInt(
									playerDP,
									skillProp,
									skillType));
						}
						catch (NoClassDefFoundError e) {
							if (ExternalPluginManager.getMcMMOPlugin() == null)
								ModDamage.addToLogRecord(OutputPreset.FAILURE, "You need mcMMO to use the skill variables.");
							else
								ModDamage.addToLogRecord(OutputPreset.FAILURE, "McMMO has changed. Please notify the ModDamage developers.");
						}
						return null;
					}
				});
	}
	
	enum SkillProperty
	{
		LEVEL {
				@Override
				int getProperty(Player player, SkillType skillType)
				{
					return ExperienceAPI.getLevel(player, skillType);
				}
			},
		XP {
				@Override
				int getProperty(Player player, SkillType skillType)
				{
					return ExperienceAPI.getXP(player, skillType);
				}
			},
		XPNEEDED {
				@Override
				int getProperty(Player player, SkillType skillType)
				{
					return ExperienceAPI.getXPToNextLevel(player, skillType);
				}
			};
		
		abstract int getProperty(Player player, SkillType skillType);
	}

	protected final SkillProperty skillProperty;
	protected final SkillType skillType;
	
	PlayerSkillInt(IDataProvider<Player> playerDP, SkillProperty skillProperty, SkillType skillType)
	{
		super(Player.class, playerDP);
		this.skillProperty = skillProperty;
		this.skillType = skillType;
	}
	
	@Override
	public Integer myGet(Player player, EventData data) throws BailException
	{
		try
		{
			return skillProperty.getProperty(player, skillType);
		}
		catch (Exception e)
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "mcMMO threw an exception: "+e);
			return 0;
		}
	}
	
	@Override
	public String toString()
	{
		return startDP + "_skill"+skillProperty+"_" + skillType.name().toLowerCase();
	}
}
