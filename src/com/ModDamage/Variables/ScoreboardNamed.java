package com.ModDamage.Variables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.scoreboard.Scoreboard;

import com.ModDamage.Scoreboards;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Parsing.BaseDataParser;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

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
					public IDataProvider<Scoreboard> parse(ScriptLine scriptLine, EventInfo info, Matcher m, StringMatcher sm)
					{
                        IDataProvider<String> nameDP = InterpolatedString.parseWord(scriptLine, word, sm.spawn(), info);
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
