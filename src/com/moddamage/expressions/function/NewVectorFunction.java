package com.ModDamage.Expressions.Function;

import java.util.regex.Pattern;

import org.bukkit.util.Vector;

import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.FunctionParser;
import com.ModDamage.Parsing.IDataProvider;

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
