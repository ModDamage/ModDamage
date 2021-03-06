package com.moddamage.alias;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.moddamage.alias.Aliaser.CollectionAliaser;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

public class CommandAliaser extends CollectionAliaser<String> 
{
	public static CommandAliaser aliaser = new CommandAliaser();
	private final Map<InfoOtherPair<String>, Collection<IDataProvider<String>>> aliasedCommands = new HashMap<InfoOtherPair<String>, Collection<IDataProvider<String>>>();
	
	public static Collection<IDataProvider<String>> match(String string, EventInfo info) {
		InfoOtherPair<String> infoPair = new InfoOtherPair<String>(string, info);
		if (aliaser.aliasedCommands.containsKey(infoPair)) return aliaser.aliasedCommands.get(infoPair);
		
		Collection<String> strings = aliaser.matchAlias(string);
		if (strings == null) return null;
		Collection<IDataProvider<String>> istrings = new ArrayList<IDataProvider<String>>();
		
		for (String str : strings)
			istrings.add(DataProvider.parse(info, String.class, str));
		
		aliaser.aliasedCommands.put(infoPair, istrings);
		
		return istrings;
	}
	
	public CommandAliaser() { super(AliasManager.Command.name()); }
	
	@Override
	public Collection<String> matchAlias(String cmd) {
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
