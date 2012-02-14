package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.Aliasing.EnchantmentAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class PlayerHasEnchantment extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.hasenchantment\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	private final DataRef<Entity> entityRef;
	private final DataRef<ModDamageElement> entityElementRef;
	protected final Collection<Enchantment> enchantments;
	public PlayerHasEnchantment(DataRef<Entity> entityRef, DataRef<ModDamageElement> entityElementRef, Collection<Enchantment> enchantments)
	{
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.enchantments = enchantments;
	}

	@Override
	public boolean evaluate(EventData data)
	{
		if(entityElementRef.get(data).matchesType(ModDamageElement.PLAYER))
			for(Enchantment enchantment : enchantments)
				if(((Player)entityRef.get(data)).getItemInHand().containsEnchantment(enchantment))
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
			DataRef<Entity> entityRef = info.get(Entity.class, name);
			DataRef<ModDamageElement> entityElementRef = info.get(ModDamageElement.class, name);
			if(entityRef != null && !enchantments.isEmpty())
				return new PlayerHasEnchantment(entityRef, entityElementRef, enchantments);
			return null;
		}
	}
}
