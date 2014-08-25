package com.moddamage.expressions.function;

import java.util.regex.Pattern;

import org.bukkit.util.Vector;

import com.moddamage.Utils;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.FunctionParser;
import com.moddamage.parsing.IDataProvider;

public class NewVectorFunction implements IDataProvider<Vector> {
	private final IDataProvider<Number>[] args;
	
	public NewVectorFunction(IDataProvider<Number>[] arguments) {
		args = arguments;
	}
	
	@Override
	public Vector get(EventData data) throws BailException {
		Number[] vals = new Number[args.length];
		for (int i = 0; i < vals.length; i++) {
			Number value = args[i].get(data);
			if (value == null)
				return null;
			
			vals[i] = value;
		}
		
		return new Vector(vals[0].doubleValue(), vals[1].doubleValue(), vals[2].doubleValue());
	}

	@Override
	public Class<? extends Vector> provides() {
		return Vector.class;
	}
	
	public static void register()
	{
		DataProvider.register(Vector.class, null, Pattern.compile("vector", Pattern.CASE_INSENSITIVE), new FunctionParser<Vector, Object>(Number.class, Number.class, Number.class)
			{
				@SuppressWarnings("unchecked")
				@Override
				protected IDataProvider<Vector> makeProvider(EventInfo info, IDataProvider<Object> nullDP, @SuppressWarnings("rawtypes") IDataProvider[] arguments)
				{
					if (nullDP != null) return null;
					
					return new NewVectorFunction((IDataProvider<Number>[])arguments);
				}
			});
	}
	
	@Override
	public String toString()
	{
		return "vector(" + Utils.joinBy(", ", args) + ")";
	}
}
