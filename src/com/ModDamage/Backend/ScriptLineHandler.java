package com.ModDamage.Backend;

public interface ScriptLineHandler
{
	public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren);
	public void done();
}
