package com.ModDamage.Variables.Ints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;

public class WorldInt extends IntegerExp
{
	public static void register()
	{
		IntegerExp.register(
				Pattern.compile("world_("+ Utils.joinBy("|", WorldPropertyMatch.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public IntegerExp getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						DataRef<World> worldRef = info.get(World.class, "world");
						if (worldRef == null) return null;
						return sm.acceptIf(new WorldInt(worldRef,
								WorldPropertyMatch.valueOf(matcher.group(1).toUpperCase())));
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
	
	private final DataRef<World> worldRef;
	private final WorldPropertyMatch propertyMatch;
	
	WorldInt(DataRef<World> worldRef, WorldPropertyMatch propertyMatch)
	{
		this.worldRef = worldRef;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		World world = worldRef.get(data);
		if (world != null)
			return propertyMatch.getValue(world);
		return 0;
	}
	
	@Override
	public void setValue(EventData data, int value)
	{
		World world = worldRef.get(data);
		if (world != null)
			propertyMatch.setValue(world, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return propertyMatch.settable;
	}
	
	@Override
	public String toString(){ return "world_" + propertyMatch.name().toLowerCase(); }
}