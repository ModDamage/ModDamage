package com.moddamage.backend;

public interface ScriptLineHandler
{
	public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren);
	public void done();
}
