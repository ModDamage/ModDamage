package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.SettableIntegerExp;

public class WorldTagInt extends SettableIntegerExp<World>
{	
	public static void register()
	{
		DataProvider.register(Integer.class, World.class, Pattern.compile("_tag(?:value)?_(\\w+)", Pattern.CASE_INSENSITIVE), new IDataParser<Integer, World>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, IDataProvider<World> worldDP, Matcher m, StringMatcher sm)
					{
						return new WorldTagInt(
								worldDP,
								m.group(1).toLowerCase());
					}
				});
	}
	
	protected final String tag;
	
	WorldTagInt(IDataProvider<World> worldDP, String tag)
	{
		super(World.class, worldDP);
		this.tag = tag;
	}
	
	
	@Override
	public Integer myGet(World world, EventData data) throws BailException
	{
		Integer value = ModDamage.getTagger().intTags.onWorld.getTagValue(world, tag);
		return value;
	}
	
	@Override
	public void mySet(World world, EventData data, Integer value)
	{
		ModDamage.getTagger().intTags.onWorld.addTag(world, tag, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return startDP + "_tag_" + tag;
	}
}