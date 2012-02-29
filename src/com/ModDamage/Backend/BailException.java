package com.ModDamage.Backend;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import com.ModDamage.Utils;

public class BailException extends Exception
{
	private static final long serialVersionUID = -6632029660081088874L;
	
	private static Set<Object> brokenObjects = new HashSet<Object>();
	
	public final boolean suppress;
	public final Object object;

	public BailException(String message, Throwable cause)
	{
		super(message, cause);
		this.object = null;
		if (cause instanceof BailException)
			this.suppress = ((BailException) cause).suppress;
		else
			this.suppress = false;
	}

	public BailException(Object object, Throwable cause)
	{
		super(cause);
		this.object = object;
		if (cause instanceof BailException)
			this.suppress = ((BailException) cause).suppress;
		else
			this.suppress = !brokenObjects.add(object);
	}
	
	@Override
	public String toString()
	{
		if (object == null)
			return getMessage() +"\n"+ getCause().toString();
		
		Throwable cause = getCause();
		String causeStr;
		if (cause instanceof BailException)
			causeStr = cause.toString();
		else
		{
			final Writer result = new StringWriter();
		    final PrintWriter printWriter = new PrintWriter(result);
		    cause.printStackTrace(printWriter);
		    causeStr = result.toString();
		}
		return "In "+ object.getClass().getSimpleName() +" "+ Utils.safeToString(object)
				+"\n"+ causeStr;
	}
}
