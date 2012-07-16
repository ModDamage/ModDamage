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
		DataProvider.register(Integer.class, World.class, Pattern.compile("_tag(?:value)?_(\\w+)", Pattern.CASE_INSENSITIVE), new IDataParser<Integer>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, IDataProvider<?> entityDP, Matcher m, StringMatcher sm)
					{
						return sm.acceptIf(new WorldTagInt(
								entityDP,
								m.group(1).toLowerCase()));
					}
				});
	}
	
	protected final String tag;
	
	WorldTagInt(IDataProvider<?> worldDP, String tag)
	{
		super(World.class, worldDP);
		this.tag = tag;
	}
	
	
	@Override
	public Integer myGet(World world, EventData data) throws BailException
	{
		return ModDamage.getTagger().getTagValue(world, tag);
	}
	
	@Override
	public void mySet(World world, EventData data, Integer value)
	{
		ModDamage.getTagger().addTag(world, tag, value);
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