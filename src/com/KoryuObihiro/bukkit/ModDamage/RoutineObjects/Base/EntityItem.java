package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageItemStack;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityItem extends Routine
{
	protected enum ItemAction
	{
		DROP(false)
		{
			@Override
			protected void doAction(Entity entity, Collection<ModDamageItemStack> items)
			{
				World world = entity.getWorld();
				for(ModDamageItemStack item : items)
					world.dropItem(entity.getLocation(), item.toItemStack());
			}
		},
		GIVE(true)
		{
			@Override
			protected void doAction(Entity entity, Collection<ModDamageItemStack> items)
			{
				((Player)entity).getInventory().addItem(ModDamageItemStack.toItemStacks(items));
			}
		},
		TAKE(true)
		{
			@Override
			protected void doAction(Entity entity, Collection<ModDamageItemStack> items)
			{
				((Player)entity).getInventory().addItem(ModDamageItemStack.toItemStacks(items));
			}
		};
		
		protected final boolean requiresPlayer;
		private ItemAction(boolean requiresPlayer){ this.requiresPlayer = requiresPlayer;}

		abstract protected void doAction(Entity entity, Collection<ModDamageItemStack> items);
	}
	
	protected final EntityReference entityReference;
	protected final ItemAction action;
	protected final Collection<ModDamageItemStack> items;
	public EntityItem(String configString, EntityReference entityReference, ItemAction action, Collection<ModDamageItemStack> items)
	{
		super(configString);
		this.entityReference = entityReference;
		this.action = action;
		this.items = items;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo) 
	{
		if(!action.requiresPlayer || entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
		{
			for(ModDamageItemStack item : items)
				item.updateAmount(eventInfo);
			action.doAction(entityReference.getEntity(eventInfo), items);
		}
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(\\w+)effect\\.(give|drop|take)Item\\.(\\w+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{	
		@Override
		public EntityItem getNew(Matcher matcher)
		{
			Collection<ModDamageItemStack> items = AliasManager.matchItemAlias(matcher.group(2));
			if(EntityReference.isValid(matcher.group(1)) && !items.isEmpty())
				return new EntityItem(matcher.group(), EntityReference.match(matcher.group(1)), ItemAction.valueOf(matcher.group(2).toUpperCase()), items);
			return null;
		}
	}
}