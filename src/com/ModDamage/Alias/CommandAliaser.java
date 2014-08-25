package com.ModDamage.Alias;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ModDamage.Alias.Aliaser.CollectionAliaser;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class CommandAliaser extends CollectionAliaser<String> 
{
	public static CommandAliaser aliaser = new CommandAliaser();
	private final Map<InfoOtherPair<String>, Collection<IDataProvider<String>>> aliasedCommands = new HashMap<InfoOtherPair<String>, Collection<IDataProvider<String>>>();
	
	public static Collection<IDataProvider<String>> match(ScriptLine scriptLine, String string, EventInfo info) {
		InfoOtherPair<String> infoPair = new InfoOtherPair<String>(string, info);
		if (aliaser.aliasedCommands.containsKey(infoPair)) return aliaser.aliasedCommands.get(infoPair);
		
		Collection<String> strings = aliaser.matchAlias(scriptLine, string);
		if (strings == null) return null;
		Collection<IDataProvider<String>> istrings = new ArrayList<IDataProvider<String>>();
		
		for (String str : strings)
			istrings.add(DataProvider.parse(scriptLine, info, String.class, str));
		
		aliaser.aliasedCommands.put(infoPair, istrings);
		
		return istrings;
	}
	
	public CommandAliaser() { super(AliasManager.Command.name()); }
	
	@Override
	public Collection<String> matchAlias(ScriptLine scriptLine, String cmd) {
		if(hasAlias(cmd))
			return getAlias(cmd);
		return Arrays.asList(cmd);
	}
	
	@Override
	protected String matchNonAlias(String valueString) { return valueString; }
	
	@Override
	public void clear()
	{
		super.clear();
		aliasedCommands.clear();
	}
}
