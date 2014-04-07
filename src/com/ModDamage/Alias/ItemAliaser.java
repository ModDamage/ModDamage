package com.ModDamage.Alias;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.ModDamage.LogUtil;
import com.ModDamage.StringMatcher;
import com.ModDamage.Alias.Aliaser.CollectionAliaser;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventInfo;

public class ItemAliaser extends CollectionAliaser<String> 
{
	public static ItemAliaser aliaser = new ItemAliaser();
	public static List<ModDamageItemStack> match(ScriptLine line, String string, EventInfo info) { return aliaser.matchAlias(line, string, info); }
	
	public ItemAliaser() { super(AliasManager.Item.name()); }
	
	@Override
	protected String matchNonAlias(String valueString)
	{
		return valueString;
	}
	
	static final Pattern commaPattern = Pattern.compile("\\s*,\\s*");
	
	
	public List<ModDamageItemStack> matchAlias(ScriptLine line, String key, EventInfo info)
	{
		Collection<String> values = getAlias(key);
		if (values == null)
		{
			if (key.startsWith("_")) {
				if (hasAlias(key))
					return Arrays.<ModDamageItemStack>asList(); // hmm, not ready yet?
				LogUtil.error(line, "Unknown alias: \"" + key + "\"");
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
				ModDamageItemStack item = ModDamageItemStack.getNewFromFront(line, info, sm.spawn());
				if (item == null) return null;
				items.add(item);
				
				if (sm.matchesFront(commaPattern))
					continue;
				if (sm.isEmpty())
					break;
				
				LogUtil.error(line, "Unidentified Yucky Stuff: \""+ sm.string +"\"");
				return null;
			}
		}
		
		return items;
	}
}
