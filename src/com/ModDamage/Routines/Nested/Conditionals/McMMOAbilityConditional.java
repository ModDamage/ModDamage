package com.ModDamage.Routines.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
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
	protected McMMOAbilityConditional(DataRef<Entity> entityRef, DataRef<ModDamageElement> entityElementRef, McMMOAbility ability) 
	{
		super(entityRef, entityElementRef);
		this.ability = ability;
	}

	@Override
	protected boolean evaluate(EventData data, mcMMO mcMMOplugin, Player player)
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
		public McMMOAbilityConditional getNew(Matcher matcher, EventInfo info)
		{
			McMMOAbility mcMMOability = null;
			for(McMMOAbility ability : McMMOAbility.values())
				if(matcher.group(2).equalsIgnoreCase(ability.name()))
					mcMMOability = ability;
			if(mcMMOability == null) ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid McMMO ability \"" + matcher.group(3) + "\"");
			String name = matcher.group(1).toLowerCase();
			DataRef<Entity> entityRef = info.get(Entity.class, name);
			DataRef<ModDamageElement> entityElementRef = info.get(ModDamageElement.class, name);
			if(entityRef != null & mcMMOability != null)
				return new McMMOAbilityConditional(entityRef, entityElementRef, mcMMOability);
			return null;
		}
	}
}
