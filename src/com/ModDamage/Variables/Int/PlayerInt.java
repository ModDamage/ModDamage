package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.SettableIntegerExp;

public class PlayerInt extends SettableIntegerExp<Player>
{
	public static void register()
	{
		DataProvider.register(Integer.class, Player.class, 
				Pattern.compile("_("+Utils.joinBy("|", PlayerIntProperty.values()) +")", Pattern.CASE_INSENSITIVE),
				new IDataParser<Integer, Player>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, Class<?> want, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
					{
						return sm.acceptIf(new PlayerInt(
								playerDP,
								PlayerIntProperty.valueOf(m.group(1).toUpperCase())));
					}
				});
	}
	
	public enum PlayerIntProperty
	{
		EXHAUSTION(true)
		{
			@Override
			public int getValue(Player player) { return (int)player.getExhaustion(); }
			
			@Override
			public void setValue(Player player, int value) { player.setExhaustion(value); }
		},
		EXPERIENCE(true)
		{
			@Override
			public int getValue(Player player) { return (int)player.getExp() * 100; }
			
			@Override
			public void setValue(Player player, int value) { player.setExp(value / 100); }
		},
		FOODLEVEL(true)
		{
			@Override
			public int getValue(Player player) { return player.getFoodLevel(); }
			
			@Override
			public void setValue(Player player, int value) { player.setFoodLevel(value); }
		},
		GAMEMODE(true)
		{
			@Override
			public int getValue(Player player) { return player.getGameMode().getValue(); }
			
			@Override
			public void setValue(Player player, int value) { player.setGameMode(org.bukkit.GameMode.getByValue(value)); }
		},
		HELD_SLOT
		{
			@Override
			public int getValue(Player player) { return player.getInventory().getHeldItemSlot(); }
		},
		LEVEL(true)
		{
			@Override
			public int getValue(Player player) { return player.getLevel(); }
			
			@Override
			public void setValue(Player player, int value) { player.setLevel(value); }
		},
		SATURATION(true)
		{
			@Override
			public int getValue(Player player) { return (int)player.getSaturation(); }
			
			@Override
			public void setValue(Player player, int value) { player.setSaturation(value); }
		},
		SLEEPTICKS
		{
			@Override
			public int getValue(Player player) { return player.getSleepTicks(); }
		},
		TOTALEXPERIENCE(true)
		{
			@Override
			public int getValue(Player player) { return player.getTotalExperience(); }
			
			@Override
			public void setValue(Player player, int value) { player.setTotalExperience(value); }
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
	

	protected final PlayerIntProperty propertyMatch;
	
	PlayerInt(IDataProvider<Player> playerDP, PlayerIntProperty propertyMatch)
	{
		super(Player.class, playerDP);
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public Integer myGet(Player player, EventData data) throws BailException
	{
		return propertyMatch.getValue(player);
	}
	
	@Override
	public void mySet(Player player, EventData data, Integer value)
	{
		propertyMatch.setValue(player, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return propertyMatch.settable;
	}
	
	@Override
	public String toString()
	{
		return startDP + "_" + propertyMatch.name().toLowerCase();
	}

}
