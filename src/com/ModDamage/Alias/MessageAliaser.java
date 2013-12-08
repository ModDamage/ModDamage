package com.ModDamage.Alias;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ModDamage.Alias.Aliaser.CollectionAliaser;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Parsing.IDataProvider;

public class MessageAliaser extends CollectionAliaser<String> 
{
	public static MessageAliaser aliaser = new MessageAliaser();
	private final Map<InfoOtherPair<String>, Collection<IDataProvider<String>>> aliasedMessages = new HashMap<InfoOtherPair<String>, Collection<IDataProvider<String>>>();
	
	public static Collection<IDataProvider<String>> match(String string, EventInfo info) {
		InfoOtherPair<String> infoPair = new InfoOtherPair<String>(string, info);
		if (aliaser.aliasedMessages.containsKey(infoPair)) return aliaser.aliasedMessages.get(infoPair);
		
		Collection<String> strings = aliaser.matchAlias(string);
		if (strings == null) return null;
		Collection<IDataProvider<String>> istrings = new ArrayList<IDataProvider<String>>();
		
		for (String str : strings)
			istrings.add(new InterpolatedString(str, info, true));
		
		aliaser.aliasedMessages.put(infoPair, istrings);
		
		return istrings;
	}
	
	public MessageAliaser() { super(AliasManager.Message.name()); }
	
	@Override
	public Collection<String> matchAlias(String msg) {
		if(hasAlias(msg))
			return getAlias(msg);
		return Arrays.asList(msg);
	}
	
	@Override
	protected String matchNonAlias(String valueString) { return valueString; }
	
	@Override
	public void clear()
	{
		super.clear();
		aliasedMessages.clear();
	}
}
