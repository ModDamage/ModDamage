package com.ModDamage.Properties;

import org.bukkit.entity.Player;

import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.Properties;
import com.ModDamage.EventInfo.SettableProperty;

public class PlayerProps //extends SettableIntegerExp<Player>
{
	public static void register()
	{
        Properties.register("exhaustion", Player.class, "getExhaustion", "setExhaustion");
        Properties.register(new SettableProperty<Integer, Player>("experience", Integer.class, Player.class) {
                public Integer get(Player player, EventData data) { return (int) (player.getExp() * 100); }
                public void set(Player player, EventData data, Integer value) {  player.setExp((float) (value / 100.0)); }
            });
        Properties.register("foodlevel", Player.class, "getFoodLevel", "setFoodLevel");
        Properties.register("gamemode", Player.class, "getGameMode", "setGameMode");
//        Properties.register("held_slot", Player.class, "", "");
        Properties.register("level", Player.class, "getLevel", "setLevel");
        Properties.register("saturation", Player.class, "getSaturation", "setSaturation");
        Properties.register("sleepticks", Player.class, "getSleepTicks");
        Properties.register("totalexperience", Player.class, "getTotalExperience", "setTotalExperience");

        Properties.register("inv", Player.class, "getInventory");
        
        
        
        

//		Properties.register(Integer.class, Player.class,
//				Pattern.compile("_("+Utils.joinBy("|", PlayerIntProperty.values()) +")", Pattern.CASE_INSENSITIVE),
//				new IDataParser<Integer, Player>()
//				{
//					@Override
//					public IDataProvider<Integer> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
//					{
//						return sm.acceptIf(new PlayerProps(
//								playerDP,
//								PlayerIntProperty.valueOf(m.group(1).toUpperCase())));
//					}
//				});
	}
	
//	public enum PlayerIntProperty
//	{
//		EXHAUSTION(true)
//		{
//			@Override
//			public int getValue(Player player) { return (int)player.getExhaustion(); }
//
//			@Override
//			public void setValue(Player player, int value) { player.setExhaustion(value); }
//		},
//		EXPERIENCE(true)
//		{
//			@Override
//			public int getValue(Player player) { return (int)player.getExp() * 100; }
//
//			@Override
//			public void setValue(Player player, int value) { player.setExp(value / 100); }
//		},
//		FOODLEVEL(true)
//		{
//			@Override
//			public int getValue(Player player) { return player.getFoodLevel(); }
//
//			@Override
//			public void setValue(Player player, int value) { player.setFoodLevel(value); }
//		},
//		GAMEMODE(true)
//		{
//			@Override
//			public int getValue(Player player) { return player.getGameMode().getValue(); }
//
//			@Override
//			public void setValue(Player player, int value) { player.setGameMode(org.bukkit.GameMode.getByValue(value)); }
//		},
//		HELD_SLOT
//		{
//			@Override
//			public int getValue(Player player) { return player.getInventory().getHeldItemSlot(); }
//		},
//		LEVEL(true)
//		{
//			@Override
//			public int getValue(Player player) { return player.getLevel(); }
//
//			@Override
//			public void setValue(Player player, int value) { player.setLevel(value); }
//		},
//		SATURATION(true)
//		{
//			@Override
//			public int getValue(Player player) { return (int)player.getSaturation(); }
//
//			@Override
//			public void setValue(Player player, int value) { player.setSaturation(value); }
//		},
//		SLEEPTICKS
//		{
//			@Override
//			public int getValue(Player player) { return player.getSleepTicks(); }
//		},
//		TOTALEXPERIENCE(true)
//		{
//			@Override
//			public int getValue(Player player) { return player.getTotalExperience(); }
//
//			@Override
//			public void setValue(Player player, int value) { player.setTotalExperience(value); }
//		};
//
//		public boolean settable = false;
//		private PlayerIntProperty(){}
//		private PlayerIntProperty(boolean settable)
//		{
//			this.settable = settable;
//		}
//
//		abstract public int getValue(Player player);
//
//		public void setValue(Player player, int value) {}
//	}
//
//
//	protected final PlayerIntProperty propertyMatch;
//
//	PlayerProps(IDataProvider<Player> playerDP, PlayerIntProperty propertyMatch)
//	{
//		super(Player.class, playerDP);
//		this.propertyMatch = propertyMatch;
//	}
//
//	@Override
//	public Integer myGet(Player player, EventData data) throws BailException
//	{
//		return propertyMatch.getValue(player);
//	}
//
//	@Override
//	public void mySet(Player player, EventData data, Integer value)
//	{
//		propertyMatch.setValue(player, value);
//	}
//
//	@Override
//	public boolean isSettable()
//	{
//		return propertyMatch.settable;
//	}
//
//	@Override
//	public String toString()
//	{
//		return startDP + "_" + propertyMatch.name().toLowerCase();
//	}

}
