package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.StringMatcher;
import com.KoryuObihiro.bukkit.ModDamage.Utils;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;

public class DynamicWorldInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("world_("+ Utils.joinBy("|", WorldPropertyMatch.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm)
					{
						return new DynamicWorldInteger(
								WorldPropertyMatch.valueOf(matcher.group(1).toUpperCase()));
					}
				});
	}
	
	protected final WorldPropertyMatch propertyMatch;
	enum WorldPropertyMatch
	{
		ONLINEPLAYERS(false) {
			@Override protected int getValue(TargetEventInfo eventInfo){
				return eventInfo.world.getPlayers().size();
			}
		},
		TIME(true) {
			@Override protected int getValue(TargetEventInfo eventInfo){
				return (int)eventInfo.world.getTime();
			}
			@Override protected void setValue(TargetEventInfo eventInfo, int value){
				eventInfo.world.setTime(value);
			}
		},
		SEED(false) {
			@Override protected int getValue(TargetEventInfo eventInfo){
				return (int)eventInfo.world.getSeed();
			}
		};
		
		public boolean settable = false;
		private WorldPropertyMatch(boolean settable){ this.settable = settable; }
		
		abstract protected int getValue(TargetEventInfo eventInfo);
		protected void setValue(TargetEventInfo eventInfo, int value) {}
	}
	
	DynamicWorldInteger(WorldPropertyMatch propertyMatch)
	{
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo){
		return propertyMatch.getValue(eventInfo);
	}
	
	@Override
	public void setValue(TargetEventInfo eventInfo, int value)
	{
		propertyMatch.setValue(eventInfo, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return propertyMatch.settable;
	}
	
	@Override
	public String toString(){ return "world_" + propertyMatch.name().toLowerCase();}
}