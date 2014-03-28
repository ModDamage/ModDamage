package com.ModDamage.Backend;

import com.ModDamage.BaseConfig;

public class ScriptLine
{
	public final BaseConfig origin;
	public final String fullLine;
	public final String line;
	public final int lineNumber;
	public final int indentLevel;
	
	public ScriptLine(BaseConfig config, String fullLine, int lineNumber)
	{
		this.fullLine = fullLine;
		this.lineNumber = lineNumber;
		this.origin = config;
		
		int ilevel = 0;
		
		int i;
		for (i = 0; i < fullLine.length(); i++)
		{
			char c = fullLine.charAt(i);
			
			if (c == ' ')
				ilevel += 1;
			else if (c == '\t')
				ilevel += 4;
			else break;
		}
		
		indentLevel = ilevel;
		
		line = fullLine.substring(i);
	}
}
