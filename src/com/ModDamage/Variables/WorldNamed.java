package com.ModDamage.Variables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Parsing.BaseDataParser;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class WorldNamed implements IDataProvider<World>
{
	public static void register()
	{
		DataProvider.register(World.class,
				Pattern.compile("worldnamed_", Pattern.CASE_INSENSITIVE),
				new BaseDataParser<World>()
				{
					@Override
					public IDataProvider<World> parse(EventInfo info, Matcher m, StringMatcher sm)
					{
                        IDataProvider<String> name = InterpolatedString.parseWord(InterpolatedString.word, sm.spawn(), info);
                        if (name == null) return null;

                        sm.accept();
						return new WorldNamed(name);
					}
				});
	}

	protected final IDataProvider<String> name;

	WorldNamed(IDataProvider<String> name)
	{
		this.name = name;
	}
	
	@Override
	public World get(EventData data) throws BailException
    {
        return Bukkit.getWorld(name.get(data));
    }
	
	@Override
	public Class<World> provides() { return World.class; }
	
	@Override
	public String toString(){ return "worldnamed_" + name; }
}
