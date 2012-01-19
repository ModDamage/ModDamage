package com.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.AliasManager;
import com.ModDamage.RoutineObjects.Nested.Conditional;

public class PlayerHasEnchantment extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.hasenchantment\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	final EntityReference entityReference;
	protected final Collection<Enchantment> enchantments;
	public PlayerHasEnchantment(EntityReference entityReference, Collection<Enchantment> enchantments)
	{
		this.entityReference = entityReference;
		this.enchantments = enchantments;
	}

	@Override
	public boolean evaluate(TargetEventInfo eventInfo)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
			for(Enchantment enchantment : enchantments)
				if(((Player)entityReference.getEntity(eventInfo)).getItemInHand().containsEnchantment(enchantment))
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
		public PlayerHasEnchantment getNew(Matcher matcher)
		{
			Collection<Enchantment> enchantments = AliasManager.matchEnchantmentAlias(matcher.group(2));
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null && !enchantments.isEmpty())
				return new PlayerHasEnchantment(reference, enchantments);
			return null;
		}
	}
}
