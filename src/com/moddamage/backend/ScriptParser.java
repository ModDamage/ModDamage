package com.moddamage.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Pattern;

import com.moddamage.LogUtil;

public class ScriptParser
{
	private LineNumberReader r;
	
	public ScriptParser(InputStream in)
	{
		r = new LineNumberReader(new InputStreamReader(in));
	}
	
	

	private ScriptLine currentLine = null;
	
	public void parseScript(ScriptLineHandler h) throws IOException
	{
		currentLine = readLine();
		
		parseHelper(h);
	}

	
	private void parseHelper(ScriptLineHandler h) throws IOException
	{
		int myIndent = currentLine.indentLevel;
		
		while (true) {
			ScriptLine nextLine = readLine();
			if (nextLine != null && (isComment(nextLine.line) || nextLine.line.isEmpty())) continue;
			
			boolean hasChildren = false;
			if (nextLine != null)
				hasChildren = nextLine.indentLevel > myIndent;
			

			ScriptLineHandler nexth = null;
			
			if (h != null) {
				try {
					nexth = h.handleLine(currentLine, hasChildren);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			currentLine = nextLine;
			
			if (hasChildren) {
				if (nexth == null && h != null) {
					LogUtil.warning(currentLine, "Unhandled child");
				}
				
				parseHelper(nexth);
			}
			else if (nexth != null)
				nexth.done();
			
			if (currentLine == null || currentLine.indentLevel < myIndent) {
				if (h != null)
					h.done();
				
				return;
			}
			
			if (currentLine.indentLevel > myIndent)
				throw new ParserError("Helper didn't handle all children");
		}
	}

	protected ScriptLine readLine() throws IOException
	{
		String line = r.readLine();
		if (line == null) return null;
		return new ScriptLine(line, r.getLineNumber());
	}
	
	Pattern indentPattern = Pattern.compile("^[ \t]*");
	
	int getIndentLevel(String line)
	{
		int indentLevel = 0;
		
		
		
		return indentLevel;
	}
	
	
	public static final Pattern commentPattern = Pattern.compile("\\s*#");
	public static boolean isComment(String str)
	{
		return commentPattern.matcher(str).lookingAt();
	}
}
