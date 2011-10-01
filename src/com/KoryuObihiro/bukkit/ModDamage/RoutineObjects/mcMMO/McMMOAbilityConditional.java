package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.mcMMO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityFalling;
import com.gmail.nossr50.mcMMO;

public class McMMOAbilityConditional extends McMMOConditionalStatement
{
	protected final McMMOAbility ability;
	protected McMMOAbilityConditional(boolean inverted, EntityReference entityReference, McMMOAbility ability) 
	{
		super(inverted, entityReference);
		this.ability = ability;
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo, mcMMO mcMMOplugin, Player player)
	{
		return ability.checkAbility(mcMMOplugin, player);
	}

	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityFalling.class, Pattern.compile("(!?)mcmmo.(\\w+).", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityFalling getNew(Matcher matcher)
	{
		if(matcher != null)
			if(EntityReference.isValid(matcher.group(2)))
				return new EntityFalling(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)));
		return null;
	}
	
	private enum McMMOAbility
	{
		Berserk,
		GigaDrillBreaker,
		God,
		GreenTerra,
		SkullSplitter,
		SerratedStrikes,
		SuperBreaker,
		SwordsPreparation,
		TreeFeller;

		public boolean checkAbility(mcMMO mcMMOplugin, Player player)
		{
			switch(this)
			{
				case Berserk: return mcMMOplugin.getPlayerProfile(player).getBerserkMode(); 
				case GigaDrillBreaker: return mcMMOplugin.getPlayerProfile(player).getGigaDrillBreakerMode(); 
				case God: return mcMMOplugin.getPlayerProfile(player).getGodMode(); 
				case GreenTerra: return mcMMOplugin.getPlayerProfile(player).getGreenTerraMode(); 
				case SkullSplitter: return mcMMOplugin.getPlayerProfile(player).getSkullSplitterMode(); 
				case SerratedStrikes: return mcMMOplugin.getPlayerProfile(player).getSerratedStrikesMode(); 
				case SuperBreaker: return mcMMOplugin.getPlayerProfile(player).getSuperBreakerMode(); 
				case SwordsPreparation: return mcMMOplugin.getPlayerProfile(player).getSwordsPreparationMode(); 
				case TreeFeller: return mcMMOplugin.getPlayerProfile(player).getTreeFellerMode(); 
				default: return false;
			}
		}
	}
}
