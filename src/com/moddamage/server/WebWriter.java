package com.moddamage.server;

import java.io.IOException;
import java.io.Writer;

public interface WebWriter
{
	void write(Writer o) throws IOException;
}
