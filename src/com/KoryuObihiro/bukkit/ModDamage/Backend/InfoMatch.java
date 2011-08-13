package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

public class InfoMatch
{
	private final MatchType dynamicMatch;
	private final List<Object> value;
	private final boolean matches;
	private final boolean forAttacker;
	
	private enum MatchType
	{
		ARMORSET(ArmorSet.class),
		BIOME(Biome.class),
		ELEMENT(ModDamageElement.class),
		ENVIRONMENT(Environment.class),
		EVENTVALUE(Integer.class),
		GROUP(String.class),
		ITEMSTACK(ItemStack.class),
		MESSAGE(String.class),
		WORLD(World.class);
		
		private final Class matchClass;
		private MatchType(Class matchClass) 
		{
			this.matchClass = matchClass;
		}
	}
	
	InfoMatch(MatchType matchType, String string)
	{
	//dynamic matching
		if(string.startsWith("%"))
		{
			//Regex can be semantically represented by ((?:attacker|target)(?:armorset|biome|element|group|itemstack|message)|(?:event(?:environment|value|world)
			Matcher matcher = Pattern.compile("%((?:a|t)(?:a|b|e|g|i|s)|e(?:e|v|w))", Pattern.CASE_INSENSITIVE).matcher(string);
			value = null;
			dynamicMatch = matchInfo(matchType, string.substring(string.length() - 2));
			matches = matchType.equals(null);
		}
	//static matching (Bukkit match, alias)
		else
		{
			value = matchStaticValue(matchType, string);
		}
	}
	
	MatchType matchInfo(MatchType matchType, String character)
	{
		if(string.equalsIgnoreCase(""))
		{
			
		}
		else if(string.equalsIgnoreCase(""))
		{
			
		}
		else if(string.equalsIgnoreCase(""))
		{
			
		}
		else if(string.equalsIgnoreCase(""))
		{
			
		}
		else if(string.equalsIgnoreCase(""))
		{
			
		}
		else if(string.equalsIgnoreCase(""))
		{
			
		}
		else if(string.equalsIgnoreCase(""))
		{
			
		}
		else if(string.equalsIgnoreCase(""))
		{
			
		}
		else if(string.equalsIgnoreCase(""))
		{
			
		}
		else if(string.equalsIgnoreCase(""))
		{
			
		}
		else if(string.equalsIgnoreCase(""))
		{
			
		}
	}
	
	private List<Object> matchStaticValue(MatchType matchType, String string)
	{
		
	}
}
