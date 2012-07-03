package com.ModDamage.External.mcMMO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Conditionals.Conditional;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.gmail.nossr50.api.AbilityAPI;

public class AbilityConditional extends Conditional
{
	public enum Ability
	{
		Berserk
		{
			@Override
			public boolean isActivated(Player player)
			{
				return AbilityAPI.berserkEnabled(player);
			}
		},
		GigaDrillBreaker
		{
			@Override
			public boolean isActivated(Player player)
			{
				return AbilityAPI.gigaDrillBreakerEnabled(player); 
			}
		},
		GreenTerra
		{
			@Override
			public boolean isActivated(Player player)
			{
				return AbilityAPI.greenTerraEnabled(player); 
			}
		},
		SkullSplitter
		{
			@Override
			public boolean isActivated(Player player)
			{
				return AbilityAPI.skullSplitterEnabled(player); 
			}
		},
		SerratedStrikes
		{
			@Override
			public boolean isActivated(Player player)
			{
				return AbilityAPI.serratedStrikesEnabled(player);
			}
		},
		SuperBreaker
		{
			@Override
			public boolean isActivated(Player player)
			{
				return AbilityAPI.superBreakerEnabled(player); 
			}
		},
		TreeFeller
		{
			@Override
			public boolean isActivated(Player player)
			{
				return AbilityAPI.treeFellerEnabled(player); 
			}
		};

		abstract public boolean isActivated(Player player);
	}
	
	public static final Pattern pattern = Pattern.compile("(\\w+).hasactive.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	protected DataRef<Player> playerRef;
	protected final Ability ability;
	
	protected AbilityConditional(String configString, DataRef<Player> playerRef, Ability ability) 
	{
		super(configString);
		this.playerRef = playerRef;
		this.ability = ability;
	}

	@Override
	protected boolean myEvaluate(EventData data)
	{
		Player player = playerRef.get(data);
		if (player == null) return false;
		
		return ability.isActivated(player);
	}
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public AbilityConditional getNew(Matcher matcher, EventInfo info)
		{
			Ability mcMMOability = null;
			for(Ability ability : Ability.values())
				if(matcher.group(2).equalsIgnoreCase(ability.name()))
					mcMMOability = ability;
			if(mcMMOability == null) ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid McMMO ability \"" + matcher.group(3) + "\"");
			String name = matcher.group(1).toLowerCase();
			DataRef<Player> entityRef = info.get(Player.class, name);
			if(entityRef != null & mcMMOability != null)
				return new AbilityConditional(matcher.group(), entityRef, mcMMOability);
			return null;
		}
	}
}
