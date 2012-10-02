package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.SettableIntegerExp;

public class LocationInt extends SettableIntegerExp<Location>
{
	public enum LocationProperty
	{
		X
		{
			@Override public int getValue(Location loc)
			{
				return loc.getBlockX();
			}
		},
		Y
		{
			@Override public int getValue(Location loc)
			{
				return loc.getBlockY();
			}
		},
		Z
		{
			@Override public int getValue(Location loc)
			{
				return loc.getBlockZ();
			}
		},
		YAW
		{
			@Override public int getValue(Location loc)
			{
				return (int) loc.getYaw();
			}
		},
		PITCH
		{
			@Override public int getValue(Location loc)
			{
				return (int) loc.getPitch();
			}
		};
		
		public boolean settable = false;
		private LocationProperty(){}
		private LocationProperty(boolean settable)
		{
			this.settable = settable;
		}
		
		abstract public int getValue(Location loc);
		public void setValue(Location loc, int value){}
		
		@Override
		public String toString()
		{
			return name().toLowerCase();
		}
	}
	
	private final LocationProperty propertyMatch;
	
	public static void register()
	{
		DataProvider.register(Integer.class, Location.class, 
				Pattern.compile("_("+ Utils.joinBy("|", LocationProperty.values()) +")", Pattern.CASE_INSENSITIVE), 
				new IDataParser<Integer, Location>()
			{
				@Override
				public IDataProvider<Integer> parse(EventInfo info, IDataProvider<Location> locDP, Matcher m, StringMatcher sm)
				{
					return sm.acceptIf(new LocationInt(
							locDP,
							LocationProperty.valueOf(m.group(1).toUpperCase())));
				}
			});
	}
	
	LocationInt(IDataProvider<Location> locDP, LocationProperty propertyMatch)
	{
		super(Location.class, locDP);
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public Integer myGet(Location loc, EventData data) throws BailException
	{
		return propertyMatch.getValue(loc);
	}
	
	@Override
	public void mySet(Location loc, EventData data, Integer value)
	{
		if(!isSettable()) return;
		
		propertyMatch.setValue(loc, value);
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