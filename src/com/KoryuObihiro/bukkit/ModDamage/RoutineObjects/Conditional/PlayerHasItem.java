package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageItemStack;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class PlayerHasItem extends EntityConditionalStatement
{
	private final boolean strict;
	private final Collection<ModDamageItemStack> items;
	public PlayerHasItem(boolean inverted, EntityReference entityReference, boolean strict, Collection<ModDamageItemStack> items)
	{
		super(inverted, entityReference);
		this.strict = strict;
		this.items = items;
	}

	@Override
	public boolean condition(TargetEventInfo eventInfo)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
		{
			for(ModDamageItemStack item : items)
				item.updateAmount(eventInfo);
			if(strict)
			{
				for(ModDamageItemStack item : items)
					if(!((Player)entityReference.getEntity(eventInfo)).getInventory().contains(item.toItemStack()))
						return false;
				return true;
			}
			else
			{
				for(ModDamageItemStack item : items)
					if(((Player)entityReference.getEntity(eventInfo)).getInventory().contains(item.toItemStack()))
						return true;
				return false;
			}
		}
		return false;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)(\\w+)\\.has(allitems|item)\\.(\\w+)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public PlayerHasItem getNew(Matcher matcher)
		{
			Collection<ModDamageItemStack> items = AliasManager.matchItemAlias(matcher.group(4));
			EntityReference reference = EntityReference.match(matcher.group(2));
			if(reference != null && !items.isEmpty())
				return new PlayerHasItem(matcher.group(1).equals("!"), reference, matcher.group(3).equalsIgnoreCase("allitems"), items);
			return null;
		}
	}
}
