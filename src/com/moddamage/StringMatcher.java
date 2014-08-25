package com.moddamage;

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
		if (string == null) throw new Error("Null string $SM50");
		if (parent == null) return; // top level
		parent.string = string;
		string = null;
	}
	
	public <T> T acceptIf(T obj)
	{
		if (obj != null) accept();
		return obj;
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
			string = "";
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


	public String matchFront(String str)
	{
		if (string.startsWith(str))
		{
			string = string.substring(str.length());
			return str;
		}
		return null;
	}
	
	public String matchAll(String str)
	{
		if (string.equals(str))
		{
			string = "";
			return str;
		}
		return null;
	}
	
	public boolean matchesFront(String str)
	{
		return matchFront(str) != null;
	}
	
	public boolean matchesAll(String str)
	{
		return matchAll(str) != null;
	}

    public String skipTo(String str)
    {
        int i = string.indexOf(str);
        if (i == -1) return null;
        String pre = string.substring(0, i);
        string = string.substring(i);
        return pre;
    }

    public String skipTo(Pattern pattern)
    {
        Matcher m = pattern.matcher(string);
        if (!m.find()) return null;
        String pre = string.substring(0, m.start());
        string = string.substring(m.start());
        return pre;
    }


	public boolean isEmpty()
	{
		return string == null || string.isEmpty();
	}
	
	public String toString() 
	{
		return string;
	}
}
