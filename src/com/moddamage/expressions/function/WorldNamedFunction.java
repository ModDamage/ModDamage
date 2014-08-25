package com.moddamage.expressions.function;

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.FunctionParser;
import com.moddamage.parsing.IDataProvider;

public class WorldNamedFunction implements IDataProvider<World>
{
	private final IDataProvider<String> nameDP;

	private WorldNamedFunction(IDataProvider<String> nameDP)
	{
		this.nameDP = nameDP;
	}

	@Override
	public World get(EventData data) throws BailException
	{
		String name = nameDP.get(data);
		if (name == null) return null;
		
		return Bukkit.getWorld(name);
	}

	@Override
	public Class<World> provides() { return World.class; }

	public static void register()
	{
		DataProvider.register(World.class, null, Pattern.compile("worldnamed", Pattern.CASE_INSENSITIVE), new FunctionParser<World, Object>(String.class)
			{
				@SuppressWarnings("unchecked")
				@Override
				protected IDataProvider<World> makeProvider(EventInfo info, IDataProvider<Object> nullDP, @SuppressWarnings("rawtypes") IDataProvider[] arguments)
				{
					if (nullDP != null) return null;
					
					return new WorldNamedFunction((IDataProvider<String>)arguments[0]);
				}
			});
	}

	@Override
	public String toString()
	{
		return "worldnamed(" + nameDP + ")";
	}
}
