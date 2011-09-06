package com.KoryuObihiro.bukkit.ModDamage.Backend.InfoMatching;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;

abstract public class InfoMatch<T>
{
	protected final boolean matches;
	protected final boolean isDynamic;
	protected final T value;//Used if static
	
	public InfoMatch(String string)
	{
	//dynamic matching
		if(string.startsWith("%"))
		{
			//TODO 0.9.6? - Generalize the event entity references so that they semantically work with future events. Explanations shouldn't be necessary for this sort of thing.
			value = null;
			isDynamic = true;
			matches = validateDynamicValue(string.substring(string.length() - 2));
		}
	//static matching (Bukkit match, alias)
		else
		{
			entityReference = false;
			isDynamic = false;
			matches = validateStaticValue(value);
			value = matches?matchStaticValue(string):null;
		}
	}
	
	abstract protected T matchStaticValue(String string);
	
	abstract protected boolean validateStaticValue(T value);
	
	public T getValue(){ return value;}
}
