package com.ModDamage.Expressions.Function;

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.FunctionParser;
import com.ModDamage.Parsing.IDataProvider;

public class PlayerNamedFunction implements IDataProvider<OfflinePlayer>
{
	private final IDataProvider<String> nameDP;

	private PlayerNamedFunction(IDataProvider<String> nameDP)
	{
		this.nameDP = nameDP;
	}

	@Override
	public OfflinePlayer get(EventData data) throws BailException
	{
		String name = nameDP.get(data);
		if (name == null) return null;
		
		return Bukkit.getOfflinePlayer(name);
	}

	@Override
	public Class<OfflinePlayer> provides() { return OfflinePlayer.class; }

	public static void register()
	{
		DataProvider.register(OfflinePlayer.class, null, Pattern.compile("playernamed", Pattern.CASE_INSENSITIVE), new FunctionParser<OfflinePlayer, Object>(String.class)
			{
				@SuppressWarnings("unchecked")
				@Override
				protected IDataProvider<OfflinePlayer> makeProvider(EventInfo info, IDataProvider<Object> nullDP, @SuppressWarnings("rawtypes") IDataProvider[] arguments)
				{
					if (nullDP != null) return null;
					
					return new PlayerNamedFunction((IDataProvider<String>)arguments[0]);
				}
			});
	}

	@Override
	public String toString()
	{
		return "playernamed(" + nameDP + ")";
	}
}
