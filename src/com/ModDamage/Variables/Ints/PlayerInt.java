package com.ModDamage.Variables.Ints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;

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
						DataRef<Player> playerRef = info.get(Player.class, name);
						if (playerRef == null) return null;
						
						return sm.acceptIf(new PlayerInt(
								playerRef,
								PlayerIntProperty.valueOf(matcher.group(2).toUpperCase())));
					}
				});
	}
	
	protected final DataRef<Player> playerRef;
	protected final PlayerIntProperty propertyMatch;
	public enum PlayerIntProperty
	{
		EXHAUSTION(true)
		{
			@Override
			public int getValue(Player player) 
			{
				return (int)player.getExhaustion();
			}
			
			@Override
			public void setValue(Player player, int value) 
			{
				player.setExhaustion(value);
			}
		},
		EXPERIENCE(true)
		{
			@Override
			public int getValue(Player player) 
			{
				return (int)player.getExp() * 100;
			}
			
			@Override
			public void setValue(Player player, int value) 
			{
				player.setExp(value / 100);
			}
		},
		FOODLEVEL(true)
		{
			@Override
			public int getValue(Player player) 
			{
				return player.getFoodLevel();
			}
			
			@Override
			public void setValue(Player player, int value) 
			{
				player.setFoodLevel(value);
			}
		},
		GAMEMODE(true)
		{
			@Override
			public int getValue(Player player) 
			{
				return player.getGameMode().getValue();
			}
			
			@Override
			public void setValue(Player player, int value) 
			{
				player.setGameMode(org.bukkit.GameMode.getByValue(value));
			}
		},
		HELD_SLOT
		{
			@Override
			public int getValue(Player player) 
			{
				return player.getInventory().getHeldItemSlot();
			}
		},
		LEVEL(true)
		{
			@Override
			public int getValue(Player player)
			{
				return player.getLevel();
			}
			
			@Override
			public void setValue(Player player, int value)
			{
				player.setLevel(value);
			}
		},
		SATURATION(true)
		{
			@Override
			public int getValue(Player player) 
			{
				return (int)player.getSaturation();
			}
			
			@Override
			public void setValue(Player player, int value) 
			{
				player.setSaturation(value);
			}
		},
		SLEEPTICKS
		{
			@Override
			public int getValue(Player player) 
			{
				return player.getSleepTicks();
			}
		},
		TOTALEXPERIENCE(true)
		{
			@Override
			public int getValue(Player player) 
			{
				return player.getTotalExperience();
			}
			
			@Override
			public void setValue(Player player, int value) 
			{
				player.setTotalExperience(value);
			}
		},
		WIELDMATERIAL(true)
		{
			@Override
			public int getValue(Player player) 
			{
				return player.getItemInHand().getTypeId();
			}
			
			@Override
			public void setValue(Player player, int value) 
			{
				player.setItemInHand(new ItemStack(value, player.getItemInHand().getAmount()));
			}
		},
		WIELDQUANTITY(true)
		{
			@Override
			public int getValue(Player player) 
			{
				return player.getItemInHand().getAmount();
			}
			
			@Override
			public void setValue(Player player, int value) 
			{
				player.setItemInHand(new ItemStack(player.getItemInHand().getType(), value));
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
	
	PlayerInt(DataRef<Player> playerRef, PlayerIntProperty propertyMatch)
	{
		this.playerRef = playerRef;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		Player player = playerRef.get(data);
		if (player == null) return 0;
		
		return propertyMatch.getValue(player);
	}
	
	@Override
	public void setValue(EventData data, int value)
	{
		Player player = playerRef.get(data);
		if (player == null) return;
		
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
