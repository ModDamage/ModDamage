package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.Utils;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;

public class DynamicPlayerInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("("+ Utils.joinBy("|", EntityReference.values()) +")_("+ 
									 Utils.joinBy("|", PlayerIntegerPropertyMatch.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DIResult getNewFromFront(Matcher matcher, String rest)
					{
						return new DIResult(new DynamicPlayerInteger(
								EntityReference.valueOf(matcher.group(1).toUpperCase()), 
								PlayerIntegerPropertyMatch.valueOf(matcher.group(2).toUpperCase())), rest);
					}
				});
	}
	
	protected final EntityReference entityReference;
	protected final PlayerIntegerPropertyMatch propertyMatch;
	public enum PlayerIntegerPropertyMatch
	{
		BleedTicks(true, true)
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
		Exhaustion(true)
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
		Experience(true)
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
		FoodLevel(true)
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
		GameMode(true)
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
		Mana(true, true)
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
		MaxMana(false, true)
		{
			@Override
			public int getValue(Player player) 
			{
				return ExternalPluginManager.getMcMMOPlugin().getPlayerProfile(player).getMaxMana();
			}
		},
		Saturation(true)
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
		SleepTicks
		{
			@Override
			public int getValue(Player player) 
			{
				return player.getSleepTicks();
			}
		},
		TotalExperience(true)
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
		WieldMaterial(true)
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
		WieldQuantity(true)
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
	
	DynamicPlayerInteger(EntityReference reference, PlayerIntegerPropertyMatch propertyMatch)
	{
		this.entityReference = reference;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
			return propertyMatch.getValue((Player)entityReference.getEntity(eventInfo));
		return 0;
	}
	
	@Override
	public void setValue(TargetEventInfo eventInfo, int value)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
			propertyMatch.setValue((Player)entityReference.getEntity(eventInfo), value);
	}
	
	@Override
	public boolean isSettable()
	{
		return propertyMatch.settable;
	}
	
	@Override
	public String toString()
	{
		return entityReference.name().toLowerCase() + "_" + propertyMatch.name().toLowerCase();
	}

}
