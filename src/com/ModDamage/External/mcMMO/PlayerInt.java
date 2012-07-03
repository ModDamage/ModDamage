package com.ModDamage.External.mcMMO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;
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
						DataRef<Player> entityRef = info.get(Player.class, name);
						if (entityRef == null) return null;
						
						return sm.acceptIf(new PlayerInt(
								entityRef,
								PlayerIntProperty.valueOf(matcher.group(2).toUpperCase())));
					}
				});
	}
	
	protected final DataRef<Player> playerRef;
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
	
	PlayerInt(DataRef<Player> entityRef, PlayerIntProperty propertyMatch)
	{
		this.playerRef = entityRef;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		Player player = (Player) playerRef.get(data);
		if(player == null) return 0;
		
		return propertyMatch.getValue(player);
	}
	
	@Override
	public void setValue(EventData data, int value)
	{
		Player player = (Player) playerRef.get(data);
		if(player == null) return;
		
		propertyMatch.setValue((Player)playerRef.get(data), value);
	}
	
	@Override
	public boolean isSettable()
	{
		return propertyMatch.settable;
	}
	
	@Override
	public String toString()
	{
		return playerRef + "_" + propertyMatch.name().toLowerCase();
	}

}
