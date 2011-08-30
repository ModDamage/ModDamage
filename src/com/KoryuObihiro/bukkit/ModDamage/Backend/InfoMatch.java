package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

public class InfoMatch
{
	private final boolean matches;
//Used if dynamic
	private final MatchType dynamicMatch;
	private final boolean forAttacker;
//Used if static
	private final Object value;
	
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
		
		private final Class<?> matchClass;
		private MatchType(Class<?> matchClass) 
		{
			this.matchClass = matchClass;
		}
	}
	
	public InfoMatch(MatchType matchType, String string)
	{
	//dynamic matching
		if(string.startsWith("%"))
		{
			//Regex can be semantically represented by 
			Matcher matcher = Pattern.compile("%((?:attacker|target)(?:armorset|biome|element|group|itemstack|message)|(?:event(?:environment|value|world)%", Pattern.CASE_INSENSITIVE).matcher(string);
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
		if(character.equalsIgnoreCase(""))
		{
			
		}
		else if(character.equalsIgnoreCase(""))
		{
			
		}
		else if(character.equalsIgnoreCase(""))
		{
			
		}
		else if(character.equalsIgnoreCase(""))
		{
			
		}
		else if(character.equalsIgnoreCase(""))
		{
			
		}
		else if(character.equalsIgnoreCase(""))
		{
			
		}
		else if(character.equalsIgnoreCase(""))
		{
			
		}
		else if(character.equalsIgnoreCase(""))
		{
			
		}
		else if(character.equalsIgnoreCase(""))
		{
			
		}
		else if(character.equalsIgnoreCase(""))
		{
			
		}
		else if(character.equalsIgnoreCase(""))
		{
			
		}
	}
	
	private List<Object> matchStaticValue(MatchType matchType, String string)
	{
		
	}
}
