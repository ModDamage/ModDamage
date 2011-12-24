package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalStatement;

public class PlayerHasEnchantment extends EntityConditionalStatement
{
	protected final Collection<Enchantment> enchantments;
	public PlayerHasEnchantment(boolean inverted, EntityReference entityReference, Collection<Enchantment> enchantments)
	{
		super(inverted, entityReference);
		this.enchantments = enchantments;
	}

	@Override
	public boolean condition(TargetEventInfo eventInfo)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
			for(Enchantment enchantment : enchantments)
				if(((Player)entityReference.getEntity(eventInfo)).getItemInHand().containsEnchantment(enchantment))
					return true;
		return false;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)(\\w+)\\.hasenchantment\\.(\\w+)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public PlayerHasEnchantment getNew(Matcher matcher)
		{
			Collection<Enchantment> enchantments = AliasManager.matchEnchantmentAlias(matcher.group(4));
			EntityReference reference = EntityReference.match(matcher.group(2));
			if(reference != null && !enchantments.isEmpty())
				return new PlayerHasEnchantment(matcher.group(1).equals("!"), reference, enchantments);
			return null;
		}
	}
}
