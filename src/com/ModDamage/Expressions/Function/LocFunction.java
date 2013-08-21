package com.ModDamage.Expressions.Function;

import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.World;

import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.FunctionParser;
import com.ModDamage.Parsing.IDataProvider;

public class LocFunction extends DataProvider<Location, World>
{
	private final IDataProvider<Double>[] args;

	private LocFunction(IDataProvider<World> worldDP, IDataProvider<Double>[] args)
	{
		super(World.class, worldDP);
		this.args = args;
	}

	@Override
	public Location get(World world, EventData data) throws BailException
	{
		double[] argValues = new double[args.length];

		for (int i = 0; i < argValues.length; i++) {
			Double value = args[i].get(data);
			if (value == null)
				return null;
			
			argValues[i] = value;
		}

		return new Location(world, argValues[0], argValues[1], argValues[2]);
	}

	@Override
	public Class<Location> provides() { return Location.class; }

	static final Pattern commaPattern = Pattern.compile("\\s*,\\s*");
	static final Pattern endPattern = Pattern.compile("\\s*\\)");
	public static void register()
	{
		DataProvider.register(Location.class, World.class, Pattern.compile("_loc(ation)?"), new FunctionParser<Location, World>(Double.class, Double.class, Double.class)
				{
			@SuppressWarnings("unchecked")
			@Override
			protected IDataProvider<Location> makeProvider(EventInfo info, IDataProvider<World> worldDP, @SuppressWarnings("rawtypes") IDataProvider[] arguments)
			{
				return new LocFunction(worldDP, arguments);
			}
		});
	}

	@Override
	public String toString()
	{
		return startDP + "_loc(" + Utils.joinBy(", ", args) + ")";
	}
}
