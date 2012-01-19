package com.KoryuObihiro.bukkit.ModDamage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringMatcher
{
	public String string;
	
	protected StringMatcher parent, child;
	
	public StringMatcher(String string)
	{
		this.string = string;
	}
	
	
	/**
	 * Creates a new StringReader attached to it's parent
	 * @param string
	 * @param parent
	 */
	protected StringMatcher(String string, StringMatcher parent)
	{
		this.string = string;
		this.parent = parent;
	}
	
	/**
	 * Create a linked StringReader that can accept and push it's parent
	 * string along.
	 * @return child StringReader
	 */
	public StringMatcher spawn()
	{
		if (child == null) child = new StringMatcher(string, this);
		else child.string = string;
		return child;
	}
	
	/**
	 * Pushes my string to the parent. Afterwards this 
	 * StringReader shouldn't be used again until it's spawned
	 */
	public void accept()
	{
		if(parent == null) return;
		parent.string = string;
		string = null;
	}
	
	public Matcher matchFront(Pattern pattern)
	{
		Matcher m = pattern.matcher(string);
		if (m.lookingAt())
		{
			string = string.substring(m.end());
			return m;
		}
		return null;
	}
	
	public Matcher matchAll(Pattern pattern)
	{
		Matcher m = pattern.matcher(string);
		if (m.matches())
		{
			string = null;
			return m;
		}
		return null;
	}
	
	public boolean matchesFront(Pattern pattern)
	{
		return matchFront(pattern) != null;
	}
	
	public boolean matchesAll(Pattern pattern)
	{
		return matchAll(pattern) != null;
	}
}
