package com.ModDamage.Expressions.Function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.World;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class BlockLocFunction extends DataProvider<Location, World>
{
	private final IDataProvider<Integer>[] args;
	
	private BlockLocFunction(IDataProvider<World> worldDP, IDataProvider<Integer>[] args)
	{
		super(World.class, worldDP);
		this.args = args;
	}

	@Override
	public Location get(World world, EventData data) throws BailException
	{
		int[] argValues = new int[args.length];
		
		for (int i = 0; i < argValues.length; i++)
			argValues[i] = args[i].get(data);
		
		return new Location(world, argValues[0], argValues[1], argValues[2]);
	}

	@Override
	public Class<Location> provides() { return Location.class; }
	
	static final Pattern commaPattern = Pattern.compile("\\s*,\\s*");
	static final Pattern endPattern = Pattern.compile("\\s*\\)");
	public static void register()
	{
		DataProvider.register(Location.class, World.class, Pattern.compile("_(block|loc)\\s*\\("), new IDataParser<Location, World>()
			{
				@Override
				public IDataProvider<Location> parse(EventInfo info, IDataProvider<World> worldDP, Matcher m,
						StringMatcher sm)
				{
					@SuppressWarnings("unchecked")
					IDataProvider<Integer>[] args = new IDataProvider[3];
					
					for (int i = 0; i < 3; i++)
					{
						IDataProvider<Integer> arg = DataProvider.parse(info, Integer.class, sm.spawn());
						if (arg == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \"" + sm.string + "\"");
							return null;
						}
						
						args[i] = arg;
						
						if (sm.matchesFront(commaPattern) != (i != 2))
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Wrong number of parameters for " + m.group(1) + " function: "+i);
							return null;
						}
					}
					
					
					Matcher endMatcher = sm.matchFront(endPattern);
					if (endMatcher == null)
					{
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Missing end paren: \"" + sm.string + "\"");
						return null;
					}
					
					return sm.acceptIf(new BlockLocFunction(worldDP, args));
				}
			});
	}

	@Override
	public String toString()
	{
		return startDP + "_loc(" + Utils.joinBy(", ", args) + ")";
	}
}
