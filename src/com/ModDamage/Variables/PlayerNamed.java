package com.ModDamage.Variables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.BaseDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.InterpolatedString;

public class PlayerNamed implements IDataProvider<OfflinePlayer>
{
    public static final Pattern word = Pattern.compile("[\\w\\[\\]]+");

	public static void register()
	{
		DataProvider.register(OfflinePlayer.class,
				Pattern.compile("playernamed_", Pattern.CASE_INSENSITIVE),
				new BaseDataParser<OfflinePlayer>()
				{
					@Override
					public IDataProvider<OfflinePlayer> parse(EventInfo info, Matcher m, StringMatcher sm)
					{
                        IDataProvider<String> name = InterpolatedString.parseWord(word, sm.spawn(), info);
                        if (name == null) return null;

                        sm.accept();
						return new PlayerNamed(name);
					}
				});
	}

	protected final IDataProvider<String> name;

	PlayerNamed(IDataProvider<String> name)
	{
		this.name = name;
	}
	
	@Override
	public OfflinePlayer get(EventData data) throws BailException
    {
        return Bukkit.getOfflinePlayer(name.get(data));
    }
	
	@Override
	public Class<OfflinePlayer> provides() { return OfflinePlayer.class; }
	
	@Override
	public String toString(){ return "playernamed_" + name; }
}
