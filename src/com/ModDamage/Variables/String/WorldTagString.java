package com.ModDamage.Variables.String;

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
import com.ModDamage.EventInfo.SettableDataProvider;

public class WorldTagString extends SettableDataProvider<String, World>
{	
	public static void register()
	{
		DataProvider.register(String.class, World.class, Pattern.compile("_stag_(\\w+)", Pattern.CASE_INSENSITIVE), new IDataParser<String, World>()
				{
					@Override
					public IDataProvider<String> parse(EventInfo info, IDataProvider<World> worldDP, Matcher m, StringMatcher sm)
					{
						return new WorldTagString(
								worldDP,
								m.group(1).toLowerCase());
					}
				});
	}
	
	protected final String tag;
	
	WorldTagString(IDataProvider<World> worldDP, String tag)
	{
		super(World.class, worldDP);
		this.tag = tag;
	}
	
	
	@Override
	public String get(World world, EventData data) throws BailException
	{
		return ModDamage.getTagger().stringTags.getTagValue(world, tag);
	}
	
	@Override
	public void set(World world, EventData data, String value)
	{
		ModDamage.getTagger().stringTags.addTag(world, tag, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return startDP + "_stag_" + tag;
	}


	@Override
	public Class<String> provides()
	{
		return String.class;
	}
}