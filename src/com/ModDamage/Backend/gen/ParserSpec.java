package com.ModDamage.Backend.gen;

import org.objectweb.asm.Type;

public class ParserSpec
{
	public String className;
	public Type provides;
	public Type startsWith;
	
	public String pattern;
	
	public Type parserType;

	public ParserSpec(String className, Type provides, Type startsWith, String pattern, Type parserType)
	{
		this.className = className;
		this.provides = provides;
		this.startsWith = startsWith;
		this.pattern = pattern;
		this.parserType = parserType;
	}
}