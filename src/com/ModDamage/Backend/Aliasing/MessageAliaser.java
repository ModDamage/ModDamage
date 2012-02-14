package com.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Collection;

import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;
import com.ModDamage.Backend.Matching.InterpolatedString;
import com.ModDamage.EventInfo.EventInfo;

public class MessageAliaser extends CollectionAliaser<String> 
{
	public static MessageAliaser aliaser = new MessageAliaser();
	public static Collection<InterpolatedString> match(String string, EventInfo info) {
		Collection<String> strings = aliaser.matchAlias(string);
		if (strings == null) return null;
		Collection<InterpolatedString> istrings = new ArrayList<InterpolatedString>();
		
		for (String str : strings)
			istrings.add(new InterpolatedString(str, info));
		
		return istrings;
	}
	
	public MessageAliaser() { super(AliasManager.Message.name()); }
	
	@Override
	public Collection<String> matchAlias(String msg) {
		if(thisMap.containsKey(msg))
			return thisMap.get(msg);
		return null; //Arrays.asList(msg);
	}
	
	@Override // just to appease "abstract"
	protected String matchNonAlias(String valueString) { return null; }
	
	//@Override
	//protected String getObjectName(DynamicMessage object){ return object.toString(); }
}
