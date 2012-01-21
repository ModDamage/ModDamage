package com.ModDamage.Routines.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Routines.Nested.Conditional;
import com.gmail.nossr50.mcMMO;

public class McMMOAbilityConditional extends McMMOConditionalStatement
{
	public static final Pattern pattern = Pattern.compile("(\\w+).hasactive.(\\w+)", Pattern.CASE_INSENSITIVE);
	protected final McMMOAbility ability;
	public enum McMMOAbility
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
	protected McMMOAbilityConditional(EntityReference entityReference, McMMOAbility ability) 
	{
		super(entityReference);
		this.ability = ability;
	}

	@Override
	protected boolean evaluate(TargetEventInfo eventInfo, mcMMO mcMMOplugin, Player player)
	{
		return ability.isActivated(mcMMOplugin, player);
	}
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public McMMOAbilityConditional getNew(Matcher matcher)
		{
			McMMOAbility mcMMOability = null;
			for(McMMOAbility ability : McMMOAbility.values())
				if(matcher.group(2).equalsIgnoreCase(ability.name()))
					mcMMOability = ability;
			if(mcMMOability == null) ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid McMMO ability \"" + matcher.group(3) + "\"");
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null & mcMMOability != null)
				return new McMMOAbilityConditional(reference, mcMMOability);
			return null;
		}
	}
}
