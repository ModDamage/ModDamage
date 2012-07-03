package com.ModDamage.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.ModDamage.Alias.EnchantmentAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class PlayerHasEnchantment extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.hasenchantment\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	private final DataRef<Player> playerRef;
	protected final Collection<Enchantment> enchantments;
	
	public PlayerHasEnchantment(String configString, DataRef<Player> playerRef, Collection<Enchantment> enchantments)
	{
		super(configString);
		this.playerRef = playerRef;
		this.enchantments = enchantments;
	}

	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		Player player = playerRef.get(data);
		if (player == null) return false;
		
		for(Enchantment enchantment : enchantments)
			if(player.getItemInHand().containsEnchantment(enchantment))
				return true;
		return false;
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public PlayerHasEnchantment getNew(Matcher matcher, EventInfo info)
		{
			Collection<Enchantment> enchantments = EnchantmentAliaser.match(matcher.group(2));
			String name = matcher.group(1).toLowerCase();
			DataRef<Player> playerRef = info.get(Player.class, name);
			if(playerRef != null && !enchantments.isEmpty())
				return new PlayerHasEnchantment(matcher.group(), playerRef, enchantments);
			return null;
		}
	}
}
