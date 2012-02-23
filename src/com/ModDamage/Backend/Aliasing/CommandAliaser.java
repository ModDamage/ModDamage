package com.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;
import com.ModDamage.Backend.Matching.InterpolatedString;
import com.ModDamage.EventInfo.EventInfo;

public class CommandAliaser extends CollectionAliaser<String> 
{
	public static CommandAliaser aliaser = new CommandAliaser();
	private final Map<InfoOtherPair<String>, Collection<InterpolatedString>> aliasedCommands = new HashMap<InfoOtherPair<String>, Collection<InterpolatedString>>();
	
	public static Collection<InterpolatedString> match(String string, EventInfo info) {
		InfoOtherPair<String> infoPair = new InfoOtherPair<String>(string, info);
		if (aliaser.aliasedCommands.containsKey(infoPair)) return aliaser.aliasedCommands.get(infoPair);
		
		Collection<String> strings = aliaser.matchAlias(string);
		if (strings == null) return null;
		Collection<InterpolatedString> istrings = new ArrayList<InterpolatedString>();
		
		for (String str : strings)
			istrings.add(new InterpolatedString(str, info, false));
		
		aliaser.aliasedCommands.put(infoPair, istrings);
		
		return istrings;
	}
	
	public CommandAliaser() { super(AliasManager.Command.name()); }
	
	@Override
	public Collection<String> matchAlias(String cmd) {
		if(thisMap.containsKey(cmd))
			return thisMap.get(cmd);
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
