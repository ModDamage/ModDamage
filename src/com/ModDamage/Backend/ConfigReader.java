package com.ModDamage.Backend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigReader
{
	private static Pattern indent = Pattern.compile("[ \t]+");
	private LineNumberReader r;
	private int currentIndent = 0;
	
	public ConfigReader(Reader reader) throws FileNotFoundException
	{
		r = new LineNumberReader(reader);
	}
	
	public int getLineNumber()
	{
		return r.getLineNumber();
	}
	
	public String readLine() throws IOException
	{
		String line = r.readLine();
		
		currentIndent = 0;
		
		Matcher m = indent.matcher(line);
		if (m.lookingAt())
		{
			String indentation = m.group();
			for (char c : indentation.toCharArray())
			{
				if (c == ' ')
					currentIndent += 1;
				else if (c == '\t')
					currentIndent = ((currentIndent / 4) + 1) * 4;
			}
		}
		
		return line;
	}
	
	public int getIndent()
	{
		return currentIndent;
	}
}
