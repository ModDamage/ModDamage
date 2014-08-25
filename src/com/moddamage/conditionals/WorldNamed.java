package com.moddamage.conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.backend.BailException;
import com.moddamage.expressions.InterpolatedString;
import org.bukkit.World;

import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

public class WorldNamed extends Conditional<World>
{
	public static final Pattern pattern = Pattern.compile("\\.named\\.", Pattern.CASE_INSENSITIVE);
    public static final Pattern wordPattern = Pattern.compile("[\\w]+");
	
	protected final Collection<IDataProvider<String>> names;

	public WorldNamed(IDataProvider<World> worldDP, Collection<IDataProvider<String>> names)
	{
		super(World.class, worldDP);
		this.names = names;
	}

	@Override
	public Boolean get(World world, EventData data) throws BailException
	{
        String worldName = world.getName();
        for (IDataProvider<String> n : names)
            if (worldName.equalsIgnoreCase(n.get(data)))
                return true;
        return false;
	}
	
	@Override
	public String toString()
	{
		return startDP + ".named." + Utils.joinBy(",", names);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, World.class, pattern, new IDataParser<Boolean, World>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<World> worldDP, Matcher m, StringMatcher sm)
				{
                    Collection<IDataProvider<String>> names = InterpolatedString.parseWordList(wordPattern, InterpolatedString.comma, sm, info);

                    return new WorldNamed(worldDP, names);
				}
			});
	}
}
