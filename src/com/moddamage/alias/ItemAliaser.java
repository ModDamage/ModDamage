package com.moddamage.alias;

import com.moddamage.LogUtil;
import com.moddamage.StringMatcher;
import com.moddamage.alias.Aliaser.CollectionAliaser;
import com.moddamage.backend.ModDamageItemStack;
import com.moddamage.eventinfo.EventInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class ItemAliaser extends CollectionAliaser<String> 
{
	public static ItemAliaser aliaser = new ItemAliaser();
	public static List<ModDamageItemStack> match(String string, EventInfo info) { return aliaser.matchAlias(string, info); }
	
	public ItemAliaser() { super(AliasManager.Item.name()); }
	
	@Override
	protected String matchNonAlias(String valueString)
	{
		return valueString;
	}
	
	static final Pattern commaPattern = Pattern.compile("\\s*,\\s*");
	
	
	public List<ModDamageItemStack> matchAlias(String key, EventInfo info)
	{
		Collection<String> values = getAlias(key);
		if (values == null)
		{
			if (key.startsWith("_")) {
				if (hasAlias(key))
					return Arrays.<ModDamageItemStack>asList(); // hmm, not ready yet?
				LogUtil.error("Unknown alias: \"" + key + "\"");
				return null;
			}
			values = new ArrayList<String>();
			values.add(key);
		}
		
		List<ModDamageItemStack> items = new ArrayList<ModDamageItemStack>();
		
		for (String itemStr : values)
		{
			StringMatcher sm = new StringMatcher(itemStr);
			while(true) {
				ModDamageItemStack item = ModDamageItemStack.getNewFromFront(info, sm.spawn());
				if (item == null) return null;
				items.add(item);
				
				if (sm.matchesFront(commaPattern))
					continue;
				if (sm.isEmpty())
					break;
				
				LogUtil.error("Unidentified Yucky Stuff: \""+ sm.string +"\"");
				return null;
			}
		}
		
		return items;
	}
}
