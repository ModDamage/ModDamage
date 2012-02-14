package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class DynamicPlayerInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("([a-z]+)_("+ 
									 Utils.joinBy("|", PlayerIntegerPropertyMatch.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						String name = matcher.group(1).toLowerCase();
						DataRef<Entity> entityRef = info.get(Entity.class, name);
						DataRef<ModDamageElement> entityElementRef = info.get(ModDamageElement.class, name);
						if (entityRef == null || entityElementRef == null) return null;
						
						return sm.acceptIf(new DynamicPlayerInteger(
								entityRef, entityElementRef,
								PlayerIntegerPropertyMatch.valueOf(matcher.group(2).toUpperCase())));
					}
				});
	}
	
	protected final DataRef<Entity> entityRef;
	protected final DataRef<ModDamageElement> entityElementRef;
	protected final PlayerIntegerPropertyMatch propertyMatch;
	public enum PlayerIntegerPropertyMatch
	{
		BLEEDTICKS(true, true)
		{
			@Override
			public int getValue(Player player) 
			{
				return ExternalPluginManager.getMcMMOPlugin().getPlayerProfile(player).getBleedTicks();
			}
			
			@Override
			public void setValue(Player player, int value) 
			{
				ExternalPluginManager.getMcMMOPlugin().getPlayerProfile(player).setBleedTicks(value);
			}
		},
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
		LEVEL
		{
			@Override
			public int getValue(Player player)
			{
				return player.getLevel();
			}
		},
		MANA(true, true)
		{
			@Override
			public int getValue(Player player) 
			{
				return ExternalPluginManager.getMcMMOPlugin().getPlayerProfile(player).getCurrentMana();
			}
			
			@Override
			public void setValue(Player player, int value) 
			{
				ExternalPluginManager.getMcMMOPlugin().getPlayerProfile(player).setMana(value);
			}
		},
		MAXMANA(false, true)
		{
			@Override
			public int getValue(Player player) 
			{
				return ExternalPluginManager.getMcMMOPlugin().getPlayerProfile(player).getMaxMana();
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
		public boolean usesMcMMO = false;
		private PlayerIntegerPropertyMatch(){}
		private PlayerIntegerPropertyMatch(boolean settable)
		{
			this.settable = settable;
		}
		private PlayerIntegerPropertyMatch(boolean settable, boolean usesMcMMO)
		{
			this.settable = settable;
			this.usesMcMMO = usesMcMMO;
		}
		
		abstract public int getValue(Player player);
		
		public void setValue(Player player, int value) {}
	}
	
	DynamicPlayerInteger(DataRef<Entity> entityRef, DataRef<ModDamageElement> entityElementRef, PlayerIntegerPropertyMatch propertyMatch)
	{
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public int getValue(EventData data)
	{
		if(entityElementRef.get(data).matchesType(ModDamageElement.PLAYER))
			return propertyMatch.getValue((Player)entityRef.get(data));
		return 0;
	}
	
	@Override
	public void setValue(EventData data, int value)
	{
		if(entityElementRef.get(data).matchesType(ModDamageElement.PLAYER))
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
