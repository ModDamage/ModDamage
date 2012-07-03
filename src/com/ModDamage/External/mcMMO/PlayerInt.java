package com.ModDamage.External.mcMMO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Matchables.EntityType;
import com.gmail.nossr50.api.ExperienceAPI;

public class PlayerInt extends IntegerExp
{
	public static void register()
	{
		IntegerExp.register(
				Pattern.compile("([a-z]+)_("+ 
									 Utils.joinBy("|", PlayerIntProperty.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public IntegerExp getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						String name = matcher.group(1).toLowerCase();
						DataRef<Entity> entityRef = info.get(Entity.class, name);
						DataRef<EntityType> entityElementRef = info.get(EntityType.class, name);
						if (entityRef == null || entityElementRef == null) return null;
						
						return sm.acceptIf(new PlayerInt(
								entityRef, entityElementRef,
								PlayerIntProperty.valueOf(matcher.group(2).toUpperCase())));
					}
				});
	}
	
	protected final DataRef<Entity> entityRef;
	protected final DataRef<EntityType> entityElementRef;
	protected final PlayerIntProperty propertyMatch;
	public enum PlayerIntProperty
	{
		POWERLEVEL(false)
		{
			@Override
			public int getValue(Player player) 
			{
				return ExperienceAPI.getPowerLevel(player);
			}
		};
		
		public boolean settable = false;
		private PlayerIntProperty(){}
		private PlayerIntProperty(boolean settable)
		{
			this.settable = settable;
		}
		
		abstract public int getValue(Player player);
		
		public void setValue(Player player, int value) {}
	}
	
	PlayerInt(DataRef<Entity> entityRef, DataRef<EntityType> entityElementRef, PlayerIntProperty propertyMatch)
	{
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		if(entityElementRef.get(data).matches(EntityType.PLAYER))
			return propertyMatch.getValue((Player)entityRef.get(data));
		return 0;
	}
	
	@Override
	public void setValue(EventData data, int value)
	{
		if(entityElementRef.get(data).matches(EntityType.PLAYER))
			propertyMatch.setValue((Player)entityRef.get(data), value);
	}
	
	@Override
	public boolean isSettable()
	{
		return propertyMatch.settable;
	}
	
	@Override
	public String toString()
	{
		return entityRef + "_" + propertyMatch.name().toLowerCase();
	}

}
