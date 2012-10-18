package com.ModDamage.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.Backend.BailException;
import com.ModDamage.Expressions.InterpolatedString;
import org.bukkit.World;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

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
