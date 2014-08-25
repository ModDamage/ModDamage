package com.moddamage.variables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.scoreboard.Scoreboard;

import com.moddamage.Scoreboards;
import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.expressions.InterpolatedString;
import com.moddamage.parsing.BaseDataParser;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

public class ScoreboardNamed implements IDataProvider<Scoreboard>
{
    public static final Pattern word = Pattern.compile("[\\w\\[\\]]+");

	public static void register()
	{
		DataProvider.register(Scoreboard.class,
				Pattern.compile("scoreboard(?:named)?_", Pattern.CASE_INSENSITIVE),
				new BaseDataParser<Scoreboard>()
				{
					@Override
					public IDataProvider<Scoreboard> parse(EventInfo info, Matcher m, StringMatcher sm)
					{
                        IDataProvider<String> nameDP = InterpolatedString.parseWord(word, sm.spawn(), info);
                        if (nameDP == null) return null;

                        sm.accept();
						return new ScoreboardNamed(nameDP);
					}
				});
	}

	protected final IDataProvider<String> nameDP;

	ScoreboardNamed(IDataProvider<String> nameDP)
	{
		this.nameDP = nameDP;
	}
	
	@Override
	public Scoreboard get(EventData data) throws BailException
    {
        String nameString = nameDP.get(data);
        if (nameString == null) return null;
        
        Scoreboard sb = Scoreboards.getNamed(nameString);
        
        return sb;
    }
	
	@Override
	public Class<Scoreboard> provides() { return Scoreboard.class; }
	
	@Override
	public String toString(){ return "scoreboard_" + nameDP; }
}
