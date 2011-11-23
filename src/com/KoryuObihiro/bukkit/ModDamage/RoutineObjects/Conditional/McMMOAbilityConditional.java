package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;
import com.gmail.nossr50.mcMMO;

public class McMMOAbilityConditional extends McMMOConditionalStatement
{
	protected final McMMOAbility ability;
	public enum McMMOAbility //
	{
		Berserk
		{
			@Override
			public boolean isActivated(mcMMO mcMMOplugin, Player player)
			{
				return mcMMOplugin.getPlayerProfile(player).getBerserkMode();
			}
		},
		GigaDrillBreaker
		{
			@Override
			public boolean isActivated(mcMMO mcMMOplugin, Player player)
			{
				return mcMMOplugin.getPlayerProfile(player).getGigaDrillBreakerMode(); 
			}
		},
		God
		{
			@Override
			public boolean isActivated(mcMMO mcMMOplugin, Player player)
			{
				return mcMMOplugin.getPlayerProfile(player).getGodMode(); 
			}
		},
		GreenTerra
		{
			@Override
			public boolean isActivated(mcMMO mcMMOplugin, Player player)
			{
				return mcMMOplugin.getPlayerProfile(player).getGreenTerraMode(); 
			}
		},
		SkullSplitter
		{
			@Override
			public boolean isActivated(mcMMO mcMMOplugin, Player player)
			{
				return mcMMOplugin.getPlayerProfile(player).getSkullSplitterMode(); 
			}
		},
		SerratedStrikes
		{
			@Override
			public boolean isActivated(mcMMO mcMMOplugin, Player player)
			{
				return mcMMOplugin.getPlayerProfile(player).getSerratedStrikesMode();
			}
		},
		SuperBreaker
		{
			@Override
			public boolean isActivated(mcMMO mcMMOplugin, Player player)
			{
				return mcMMOplugin.getPlayerProfile(player).getSuperBreakerMode(); 
			}
		},
		SwordsPreparation
		{
			@Override
			public boolean isActivated(mcMMO mcMMOplugin, Player player)
			{
				return mcMMOplugin.getPlayerProfile(player).getSwordsPreparationMode();
			}
		},
		TreeFeller
		{
			@Override
			public boolean isActivated(mcMMO mcMMOplugin, Player player)
			{
				return mcMMOplugin.getPlayerProfile(player).getTreeFellerMode(); 
			}
		};

		abstract public boolean isActivated(mcMMO mcMMOplugin, Player player);
	}
	protected McMMOAbilityConditional(boolean inverted, EntityReference entityReference, McMMOAbility ability) 
	{
		super(inverted, entityReference);
		this.ability = ability;
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo, mcMMO mcMMOplugin, Player player)
	{
		return ability.isActivated(mcMMOplugin, player);
	}
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)(\\w+).hasactive.(\\w+)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public McMMOAbilityConditional getNew(Matcher matcher)
		{
			McMMOAbility mcMMOability = null;
			for(McMMOAbility ability : McMMOAbility.values())
				if(matcher.group(3).equalsIgnoreCase(ability.name()))
					mcMMOability = ability;
			if(mcMMOability == null) ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid McMMO ability \"" + matcher.group(3) + "\"");
			if(EntityReference.isValid(matcher.group(2)) & mcMMOability != null)
				return new McMMOAbilityConditional(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), mcMMOability);
			return null;
		}
	}
}
