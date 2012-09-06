package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.SettableIntegerExp;

public class WorldInt extends SettableIntegerExp<World>
{
	public static void register()
	{
		DataProvider.register(Integer.class, World.class, 
				Pattern.compile("_("+ Utils.joinBy("|", WorldPropertyMatch.values()) +")", Pattern.CASE_INSENSITIVE),
				new IDataParser<Integer, World>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, IDataProvider<World> worldDP, Matcher m, StringMatcher sm)
					{
						return sm.acceptIf(new WorldInt(worldDP,
								WorldPropertyMatch.valueOf(m.group(1).toUpperCase())));
					}
				});
	}
	
	enum WorldPropertyMatch
	{
		ONLINEPLAYERS(false) {
			@Override protected int getValue(World world){
				return world.getPlayers().size();
			}
		},
		TIME(true) {
			@Override protected int getValue(World world){
				return (int)world.getTime();
			}
			@Override protected void setValue(World world, int value){
				world.setTime(value);
			}
		},
		FULLTIME(true) {
			@Override protected int getValue(World world){
				return (int)world.getFullTime();
			}
			@Override protected void setValue(World world, int value){
				world.setFullTime(value);
			}
		},
		DAY(true) {
			@Override protected int getValue(World world){
				return (int)(world.getFullTime() / 24000);
			}
			@Override protected void setValue(World world, int value){
				world.setFullTime(value * 24000 + world.getTime());
			}
		},
		MOONPHASE(false) {
			@Override protected int getValue(World world){
				return (int)(world.getFullTime() / 24000) % 8;
			}
		},
		SEED(false) {
			@Override protected int getValue(World world){
				return (int)world.getSeed();
			}
		};
		
		public boolean settable = false;
		private WorldPropertyMatch(boolean settable){ this.settable = settable; }
		
		abstract protected int getValue(World world);
		protected void setValue(World world, int value) {}
	}
	
	private final WorldPropertyMatch propertyMatch;
	
	WorldInt(IDataProvider<World> worldDP, WorldPropertyMatch propertyMatch)
	{
		super(World.class, worldDP);
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public Integer myGet(World world, EventData data) throws BailException
	{
		return propertyMatch.getValue(world);
	}
	
	@Override
	public void mySet(World world, EventData data, Integer value)
	{
		propertyMatch.setValue(world, value);
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